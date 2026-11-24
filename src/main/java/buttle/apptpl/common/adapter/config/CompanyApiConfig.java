package buttle.apptpl.common.adapter.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import buttle.apptpl.common.adapter.dto.CompanyApiInfo;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 회원사 API 호출 설정
 * 
 * <p>WebClient 및 캐시 설정을 담당합니다.</p>
 * 
 * @since 2026-01-15
 * @author ID-ABO 개발팀
 */
@Slf4j
@Configuration
public class CompanyApiConfig {
    
    /**
     * WebClient.Builder 빈 생성
     * 
     * @return WebClient.Builder
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        // HTTP Client 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 연결 타임아웃 10초
                .responseTimeout(Duration.ofSeconds(30)) // 응답 타임아웃 30초
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS))
                );
        
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
    
    /**
     * 회원사 API 정보 캐시 빈 생성
     * 
     * <p>캐시 설정:</p>
     * <ul>
     *   <li>최대 크기: 1000개</li>
     *   <li>만료 시간: 1시간 (마지막 접근 후)</li>
     * </ul>
     * 
     * @return Caffeine 캐시
     */
    @Bean
    public Cache<String, CompanyApiInfo> companyApiCache() {
        log.info("회원사 API 정보 캐시 초기화");
        
        return Caffeine.newBuilder()
                .maximumSize(1000) // 최대 1000개 회원사 정보 캐싱
                .expireAfterAccess(1, TimeUnit.HOURS) // 1시간 동안 접근이 없으면 제거
                .recordStats() // 캐시 통계 기록
                .build();
    }
}
