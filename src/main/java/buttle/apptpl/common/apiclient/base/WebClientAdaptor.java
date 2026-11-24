package buttle.apptpl.common.apiclient.base;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Slf4j
public class WebClientAdaptor {
    
    private final WebClientConfigProperties webClientConfigProperties;
    private final Map<String, WebClient> webClientCache = new HashMap<>();
    private final Object cacheLock = new Object();

    public WebClientAdaptor(WebClientConfigProperties webClientConfigProperties) {
        this.webClientConfigProperties = webClientConfigProperties;
    }

    public WebClient getWebClient(String serverName) {
        // 캐시에서 먼저 확인 (Double-checked locking 패턴)
        WebClient cachedClient = webClientCache.get(serverName);
        if (cachedClient != null) {
            return cachedClient;
        }

        synchronized (cacheLock) {
            // 동기화 블록 내에서 다시 확인 (다른 스레드가 이미 생성했을 수 있음)
            cachedClient = webClientCache.get(serverName);
            if (cachedClient != null) {
                return cachedClient;
            }

            try {
                WebClientServerConfig serverConfig = webClientConfigProperties.getServers().get(serverName);
                if (serverConfig == null) {
                    throw new IllegalArgumentException("WebClient 설정을 찾을 수 없습니다: " + serverName +
                        ". 사용 가능한 서버: " + webClientConfigProperties.getServers().keySet());
                }

                HttpClient httpClient = createHttpClient(serverConfig);
                WebClient webClient = WebClient.builder()
                        .baseUrl(serverConfig.getBaseUrl())
                        .clientConnector(new ReactorClientHttpConnector(httpClient))
                        .build();

                // 캐시에 저장
                webClientCache.put(serverName, webClient);
                        
                log.info("WebClient 생성 및 캐시 완료 - 서버: {}, URL: {}, HTTPS: {}", 
                    serverName, serverConfig.getBaseUrl(), serverConfig.getBaseUrl().startsWith("https"));

                return webClient;

            } catch (IllegalArgumentException e) {
                log.error("WebClient 설정 오류 - 서버: {}, 원인: {}", serverName, e.getMessage());
                throw e;
            } catch (SSLException e) {
                log.error("WebClient SSL 설정 오류 - 서버: {}, SSL 오류: {}", serverName, e.getMessage());
                throw new RuntimeException("SSL 설정 실패: " + serverName, e);
            } catch (IllegalStateException e) {
                log.error("WebClient 상태 오류 - 서버: {}, 상태 오류: {}", serverName, e.getMessage());
                throw new RuntimeException("WebClient 상태 오류: " + serverName, e);
            } catch (OutOfMemoryError e) {
                log.error("WebClient 생성 중 메모리 부족 - 서버: {}", serverName);
                throw new RuntimeException("메모리 부족으로 WebClient 생성 실패: " + serverName, e);
            } catch (Exception e) {
                log.error("WebClient 생성 중 예상치 못한 오류 - 서버: {}, 오류 타입: {}, 메시지: {}", 
                    serverName, e.getClass().getSimpleName(), e.getMessage());
                throw new RuntimeException("WebClient 초기화 실패: " + serverName, e);
            }
        }
    }


    
    private HttpClient createHttpClient(WebClientServerConfig serverConfig) throws SSLException {
        try {
            // 연결 풀 설정
            ConnectionProvider connectionProvider = ConnectionProvider.builder("webclient-pool")
                    .maxConnections(serverConfig.getMaxConnections())
                    .maxIdleTime(Duration.ofMillis(serverConfig.getMaxIdleTime()))
                    .build();
            
            HttpClient httpClient = HttpClient.create(connectionProvider)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, serverConfig.getTimeoutMillis())
                    .responseTimeout(Duration.ofMillis(serverConfig.getTimeoutMillis()))
                    .doOnConnected(conn ->
                            conn.addHandlerLast(new ReadTimeoutHandler(serverConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS))
                                    .addHandlerLast(new WriteTimeoutHandler(serverConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS)));
            
            // HTTP/2 지원 설정
            if (serverConfig.isHttp2Enabled()) {
                httpClient = httpClient.protocol(reactor.netty.http.HttpProtocol.H2C, reactor.netty.http.HttpProtocol.HTTP11);
                log.debug("HTTP/2 프로토콜 활성화 - 서버: {}", serverConfig.getBaseUrl());
            }
            
            // HTTPS 통신을 위한 SSL 설정
            if (serverConfig.getBaseUrl().startsWith("https")) {
                SslContext sslContext = createSslContext(serverConfig);
                httpClient = httpClient.secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
                log.debug("HTTPS SSL 컨텍스트 적용 - 서버: {}", serverConfig.getBaseUrl());
            }
            
            return httpClient;
            
        } catch (SSLException e) {
            log.error("HttpClient SSL 설정 실패 - 서버: {}, SSL 오류: {}", serverConfig.getBaseUrl(), e.getMessage());
            throw e; // SSLException은 그대로 전파
        } catch (IllegalArgumentException e) {
            log.error("HttpClient 설정 값 오류 - 서버: {}, 설정 오류: {}", serverConfig.getBaseUrl(), e.getMessage());
            throw new IllegalStateException("HttpClient 설정 오류: " + serverConfig.getBaseUrl(), e);
        } catch (IllegalStateException e) {
            log.error("HttpClient 상태 오류 - 서버: {}, 상태 오류: {}", serverConfig.getBaseUrl(), e.getMessage());
            throw e;
        } catch (OutOfMemoryError e) {
            log.error("HttpClient 생성 중 메모리 부족 - 서버: {}", serverConfig.getBaseUrl());
            throw new RuntimeException("메모리 부족으로 HttpClient 생성 실패: " + serverConfig.getBaseUrl(), e);
        } catch (Exception e) {
            log.error("HttpClient 생성 중 예상치 못한 오류 - 서버: {}, 오류 타입: {}, 메시지: {}", 
                serverConfig.getBaseUrl(), e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException("HttpClient 생성 실패: " + serverConfig.getBaseUrl(), e);
        }
    }
    
    private SslContext createSslContext(WebClientServerConfig serverConfig) throws SSLException {
        try {
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
            
            // HTTP/2 지원을 위한 ALPN 설정
            if (serverConfig.isHttp2Enabled()) {
                try {
                    // HTTP/2와 HTTP/1.1을 모두 지원하도록 ALPN 설정
                    sslContextBuilder.applicationProtocolConfig(
                        new ApplicationProtocolConfig(
                            ApplicationProtocolConfig.Protocol.ALPN,
                            ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                            "h2", "http/1.1"
                        )
                    );
                    log.debug("HTTP/2 ALPN 설정 적용");
                } catch (IllegalArgumentException e) {
                    log.warn("HTTP/2 ALPN 설정 실패, HTTP/1.1로 폴백 - 서버: {}, 오류: {}", 
                        serverConfig.getBaseUrl(), e.getMessage());
                }
            }
            
            // SSL 설정 옵션에 따른 처리 (전역 설정 우선 적용)
            boolean trustAll = webClientConfigProperties.isGlobalSslTrustAll() || serverConfig.isSslTrustAll();
            
            if (trustAll) {
                // 개발/테스트 환경: 모든 인증서 신뢰 (보안 위험)
                String reason = webClientConfigProperties.isGlobalSslTrustAll() ? "전역 설정" : "개별 서버 설정";
                log.warn("SSL 인증서 검증 비활성화 ({}) - 개발 환경에서만 사용하세요: {}", reason, serverConfig.getBaseUrl());
                sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
            } else {
                // 운영 환경: 시스템 기본 인증서 스토어 사용
                log.info("SSL 인증서 검증 활성화 - 시스템 기본 신뢰 저장소 사용: {}", serverConfig.getBaseUrl());
            }
            
            // 커스텀 키스토어가 설정된 경우
            if (serverConfig.getKeystorePath() != null && serverConfig.getKeystorePassword() != null) {
                // TODO: 커스텀 키스토어 로드 로직 구현
                log.info("커스텀 키스토어 사용: {}", serverConfig.getKeystorePath());
            }
            
            // 커스텀 트러스트스토어가 설정된 경우  
            if (serverConfig.getTruststorePath() != null && serverConfig.getTruststorePassword() != null) {
                // TODO: 커스텀 트러스트스토어 로드 로직 구현
                log.info("커스텀 트러스트스토어 사용: {}", serverConfig.getTruststorePath());
            }
            
            return sslContextBuilder.build();
            
        } catch (SSLException e) {
            log.error("SSL 컨텍스트 생성 실패 - 서버: {}, SSL 오류: {}", serverConfig.getBaseUrl(), e.getMessage());
            throw e; // SSLException은 그대로 전파
        } catch (IllegalArgumentException e) {
            log.error("SSL 설정 값 오류 - 서버: {}, 설정 오류: {}", serverConfig.getBaseUrl(), e.getMessage());
            throw new SSLException("SSL 설정 오류: " + serverConfig.getBaseUrl(), e);
        } catch (IllegalStateException e) {
            log.error("SSL 컨텍스트 상태 오류 - 서버: {}, 상태 오류: {}", serverConfig.getBaseUrl(), e.getMessage());
            throw new SSLException("SSL 컨텍스트 상태 오류: " + serverConfig.getBaseUrl(), e);
        } catch (SecurityException e) {
            log.error("SSL 보안 정책 오류 - 서버: {}, 보안 오류: {}", serverConfig.getBaseUrl(), e.getMessage());
            throw new SSLException("SSL 보안 정책 오류: " + serverConfig.getBaseUrl(), e);
        } catch (OutOfMemoryError e) {
            log.error("SSL 컨텍스트 생성 중 메모리 부족 - 서버: {}", serverConfig.getBaseUrl());
            throw new SSLException("메모리 부족으로 SSL 컨텍스트 생성 실패: " + serverConfig.getBaseUrl());
        } catch (Exception e) {
            log.error("SSL 컨텍스트 생성 중 예상치 못한 오류 - 서버: {}, 오류 타입: {}, 메시지: {}", 
                serverConfig.getBaseUrl(), e.getClass().getSimpleName(), e.getMessage());
            throw new SSLException("SSL 컨텍스트 생성 실패: " + serverConfig.getBaseUrl(), e);
        }
    }
    
    /**
     * 서버 연결 상태 확인
     */
    public boolean isServerAvailable(String serverName) {
        try {
            getWebClient(serverName); // WebClient 존재 여부만 확인
            // TODO: Health check endpoint 호출 구현 필요
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("서버 설정을 찾을 수 없음 - 서버: {}, 오류: {}", serverName, e.getMessage());
            return false;
        } catch (RuntimeException e) {
            // WebClient 생성 과정에서 발생하는 RuntimeException들 처리
            if (e.getCause() instanceof SSLException) {
                log.warn("서버 SSL 설정 오류로 연결 불가 - 서버: {}, SSL 오류: {}", serverName, e.getCause().getMessage());
            } else if (e.getCause() instanceof OutOfMemoryError) {
                log.error("메모리 부족으로 서버 연결 확인 실패 - 서버: {}", serverName);
            } else {
                log.warn("서버 연결 확인 실패 - 서버: {}, 오류: {}", serverName, e.getMessage());
            }
            return false;
        } catch (OutOfMemoryError e) {
            log.error("메모리 부족으로 서버 연결 확인 실패 - 서버: {}", serverName);
            return false;
        } catch (Exception e) {
            log.warn("서버 연결 확인 중 예상치 못한 오류 - 서버: {}, 오류 타입: {}, 메시지: {}", 
                serverName, e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
    
    /**
     * 특정 서버의 WebClient 캐시 무효화 (설정 변경 시 사용)
     */
    public void invalidateCache(String serverName) {
        synchronized (cacheLock) {
            WebClient removed = webClientCache.remove(serverName);
            if (removed != null) {
                log.info("WebClient 캐시 무효화 완료 - 서버: {}", serverName);
            }
        }
    }
    
    /**
     * 모든 WebClient 캐시 무효화
     */
    public void invalidateAllCache() {
        synchronized (cacheLock) {
            int size = webClientCache.size();
            webClientCache.clear();
            log.info("모든 WebClient 캐시 무효화 완료 - {} 개 항목", size);
        }
    }
    
    /**
     * 캐시된 WebClient 개수 반환
     */
    public int getCacheSize() {
        return webClientCache.size();
    }
    
    /**
     * 모든 WebClient 정리 (애플리케이션 종료 시)
     */
    public void shutdown() {
        synchronized (cacheLock) {
            int size = webClientCache.size();
            webClientCache.clear();
            log.info("모든 WebClient 정리 완료 - {} 개 항목", size);
        }
    }

    
}
