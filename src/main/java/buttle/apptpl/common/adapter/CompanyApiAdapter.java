package buttle.apptpl.common.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import buttle.apptpl.common.adapter.dto.CompanyApiInfo;
import buttle.apptpl.common.adapter.exception.CompanyApiException;
import buttle.apptpl.common.adapter.exception.CompanyServerNotAssignedException;
import buttle.apptpl.common.adapter.service.CompanyApiCacheService;

import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;

import java.time.Duration;
import java.util.Map;

/**
 * 회원사 API 호출 어댑터
 * 
 * <p>회원사별로 등록된 외부 API를 호출하는 공통 어댑터입니다.</p>
 * <ul>
 *   <li>GET, POST, PUT, DELETE 메서드 지원</li>
 *   <li>회원사별 API 정보 캐시 활용</li>
 *   <li>타임아웃 설정: 30초</li>
 *   <li>예외 처리 및 로깅</li>
 * </ul>
 * 
 * @since 2026-01-15
 * @author ID-ABO 개발팀
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyApiAdapter {
    
    private final CompanyApiCacheService companyApiCacheService;
    private final WebClient.Builder webClientBuilder;

    @Value("${company-api.local.enabled:false}")
    private boolean localEnabled;

    @Value("${company-api.local.company-name:Local Test}")
    private String localCompanyName;

    @Value("${company-api.local.api-server-url:}")
    private String localApiServerUrl;

    /** desk 공유 내부키(= desk app.internal-api-key). 회원사별 토큰 대신 이 공유키로 X-Api-Key 세팅 (09b (C) 항목10) */
    @Value("${app.outbound-api-key:local-api-key}")
    private String outboundApiKey;
    
    /** API 호출 타임아웃 (초) */
    private static final int TIMEOUT_SECONDS = 30;
    
    /**
     * GET 요청 (쿼리 파라미터)
     * 
     * @param <T> 응답 타입
     * @param companyId 회원사ID
     * @param endpoint API 엔드포인트 경로
     * @param queryParams 쿼리 파라미터 (null 가능)
     * @param responseType 응답 클래스 타입
     * @return API 응답 객체
     * @throws CompanyApiException 회원사 API 호출 오류
     */
    public <T> T get(String companyId, String endpoint, Map<String, Object> queryParams, Class<T> responseType) {
        log.debug("회원사 API GET 호출 - companyId: {}, endpoint: {}", companyId, endpoint);
        
        try {
            // 1. 회원사 API 정보 조회
            CompanyApiInfo apiInfo = getCompanyApiInfo(companyId);
            
            // desk 서버 미지정은 장애가 아니다 — 읽기는 "없음", 쓰기는 409 로 갈린다
            requireAssignedServer(apiInfo, endpoint, false);
            
            // 2. WebClient 생성
            WebClient webClient = createWebClient(apiInfo);
            
            // 3. GET 요청 빌더 생성
            WebClient.RequestHeadersSpec<?> requestSpec = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(endpoint);
                        // 쿼리 파라미터 추가
                        if (!CollectionUtils.isEmpty(queryParams)) {
                            queryParams.forEach((key, value) -> {
                                if (value != null) {
                                    uriBuilder.queryParam(key, value);
                                }
                            });
                        }
                        return uriBuilder.build();
                    });
            
            // 4. API 호출 및 응답 처리
            T response = requestSpec
                    .retrieve()
                    .bodyToMono(responseType)
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .onErrorMap(this::handleError)
                    .block();
            
            log.debug("회원사 API GET 호출 완료 - companyId: {}, endpoint: {}", companyId, endpoint);
            return response;
            
        } catch (Exception e) {
            log.error("회원사 API GET 호출 실패 - companyId: {}, endpoint: {}", companyId, endpoint, e);
            throw convertToCompanyApiException(companyId, endpoint, e);
        }
    }
    
    
    /**
     * POST 요청 (JSON 본문)
     * 
     * @param <T> 응답 타입
     * @param companyId 회원사ID
     * @param endpoint API 엔드포인트 경로
     * @param requestBody 요청 본문 객체
     * @param responseType 응답 클래스 타입
     * @return API 응답 객체
     * @throws CompanyApiException 회원사 API 호출 오류
     */
    public <T> T post(String companyId, String endpoint, Object requestBody, Class<T> responseType) {
        log.debug("회원사 API POST 호출 - companyId: {}, endpoint: {}", companyId, endpoint);
        
        try {
            // 1. 회원사 API 정보 조회
            CompanyApiInfo apiInfo = getCompanyApiInfo(companyId);
            
            // desk 서버 미지정은 장애가 아니다 — 읽기는 "없음", 쓰기는 409 로 갈린다
            requireAssignedServer(apiInfo, endpoint, true);
            
            // 2. WebClient 생성
            WebClient webClient = createWebClient(apiInfo);
            
            // 3. POST 요청
            T response = webClient.post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(responseType)
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .onErrorMap(this::handleError)
                    .block();
            
            log.debug("회원사 API POST 호출 완료 - companyId: {}, endpoint: {}", companyId, endpoint);
            return response;
            
        } catch (Exception e) {
            log.error("회원사 API POST 호출 실패 - companyId: {}, endpoint: {}", companyId, endpoint, e);
            throw convertToCompanyApiException(companyId, endpoint, e);
        }
    }
    
    /**
     * PUT 요청 (JSON 본문)
     * 
     * @param <T> 응답 타입
     * @param companyId 회원사ID
     * @param endpoint API 엔드포인트 경로
     * @param requestBody 요청 본문 객체
     * @param responseType 응답 클래스 타입
     * @return API 응답 객체
     * @throws CompanyApiException 회원사 API 호출 오류
     */
    public <T> T put(String companyId, String endpoint, Object requestBody, Class<T> responseType) {
        log.debug("회원사 API PUT 호출 - companyId: {}, endpoint: {}", companyId, endpoint);
        
        try {
            // 1. 회원사 API 정보 조회
            CompanyApiInfo apiInfo = getCompanyApiInfo(companyId);
            
            // desk 서버 미지정은 장애가 아니다 — 읽기는 "없음", 쓰기는 409 로 갈린다
            requireAssignedServer(apiInfo, endpoint, true);
            
            // 2. WebClient 생성
            WebClient webClient = createWebClient(apiInfo);
            
            // 3. PUT 요청
            T response = webClient.put()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(responseType)
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .onErrorMap(this::handleError)
                    .block();
            
            log.debug("회원사 API PUT 호출 완료 - companyId: {}, endpoint: {}", companyId, endpoint);
            return response;
            
        } catch (Exception e) {
            log.error("회원사 API PUT 호출 실패 - companyId: {}, endpoint: {}", companyId, endpoint, e);
            throw convertToCompanyApiException(companyId, endpoint, e);
        }
    }
    
    /**
     * DELETE 요청
     * 
     * @param <T> 응답 타입
     * @param companyId 회원사ID
     * @param endpoint API 엔드포인트 경로
     * @param responseType 응답 클래스 타입
     * @return API 응답 객체
     * @throws CompanyApiException 회원사 API 호출 오류
     */
    public <T> T delete(String companyId, String endpoint, Class<T> responseType) {
        log.debug("회원사 API DELETE 호출 - companyId: {}, endpoint: {}", companyId, endpoint);
        
        try {
            // 1. 회원사 API 정보 조회
            CompanyApiInfo apiInfo = getCompanyApiInfo(companyId);
            
            // desk 서버 미지정은 장애가 아니다 — 읽기는 "없음", 쓰기는 409 로 갈린다
            requireAssignedServer(apiInfo, endpoint, true);
            
            // 2. WebClient 생성
            WebClient webClient = createWebClient(apiInfo);
            
            // 3. DELETE 요청
            T response = webClient.delete()
                    .uri(endpoint)
                    .retrieve()
                    .bodyToMono(responseType)
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .onErrorMap(this::handleError)
                    .block();
            
            log.debug("회원사 API DELETE 호출 완료 - companyId: {}, endpoint: {}", companyId, endpoint);
            return response;
            
        } catch (Exception e) {
            log.error("회원사 API DELETE 호출 실패 - companyId: {}, endpoint: {}", companyId, endpoint, e);
            throw convertToCompanyApiException(companyId, endpoint, e);
        }
    }
    
    /**
     * 회원사 API 정보 조회
     * 
     * @param companyId 회원사ID
     * @return 회원사 API 정보
     * @throws IllegalArgumentException 회원사 정보가 없는 경우
     */
    private CompanyApiInfo getCompanyApiInfo(String companyId) {
        // 로컬 테스트 모드인 경우 고정값 반환
        if (localEnabled) {
            log.debug("로컬 설정 기반 회원사 정보 반환 - companyId: {}", companyId);
            return CompanyApiInfo.builder()
                    .companyId(companyId)
                    .companyName(localCompanyName)
                    .apiServerUrl(localApiServerUrl)
                    .useYn("Y")
                    .build();
        }

        CompanyApiInfo apiInfo = companyApiCacheService.getCompanyApiInfo(companyId);
        
        if (apiInfo == null) {
            log.error("회원사 API 정보를 찾을 수 없음 - companyId: {}", companyId);
            throw new IllegalArgumentException("회원사 API 정보를 찾을 수 없습니다: " + companyId);
        }
        
        if (!"Y".equals(apiInfo.getUseYn())) {
            log.error("비활성화된 회원사 - companyId: {}", companyId);
            throw new IllegalStateException("비활성화된 회원사입니다: " + companyId);
        }
        
        return apiInfo;
    }

    /**
     * desk 서버가 지정되어 있는지 확인한다. <b>지정되지 않은 것은 장애가 아니다.</b>
     *
     * <p>Gateway 연계 전용 회원사(예: {@code micohelp})는 desk 배포 대상이 아니라
     * {@code desk_svr_id}·{@code api_svr_url} 이 비는 것이 정상이다(V44 가 {@code gw_svr_id} 를
     * 따로 둔 이유다). 종전에는 그대로 WebClient 까지 가서 base URL 이 null 인 채 호출이 터졌고,
     * {@link #isServerUnavailable} 이 그것을 <i>"회원사 API 서버에 연결할 수 없습니다"</i> 로
     * 바꿨다 — <b>정상 상태가 서버 장애로 보였다.</b>
     *
     * <p>읽기와 쓰기를 갈라야 한다. 읽기는 "없음"이 참이지만, 쓰기를 조용히 성공시키면
     * 운영자는 저장된 줄 안다. 그래서 쓰기는 409 로 분명히 거절한다.
     *
     * @param write 쓰기 요청이면 true
     */
    private void requireAssignedServer(CompanyApiInfo apiInfo, String endpoint, boolean write) {
        String baseUrl = apiInfo.getApiServerUrl();
        if (baseUrl != null && !baseUrl.isBlank()) {
            return;
        }
        if (write) {
            throw new CompanyApiException(
                    apiInfo.getCompanyId(), endpoint, HttpStatus.CONFLICT.value(),
                    "이 회원사에는 desk 서버가 지정되어 있지 않아 저장할 수 없습니다. "
                            + "[기본정보] 탭에서 '대상 desk 서버'를 먼저 지정해 주세요.",
                    null);
        }
        throw new CompanyServerNotAssignedException(apiInfo.getCompanyId(), endpoint);
    }
    
    /**
     * WebClient 생성
     * 
     * @param apiInfo 회원사 API 정보
     * @return WebClient 인스턴스
     */
    private WebClient createWebClient(CompanyApiInfo apiInfo) {
        return webClientBuilder
                .baseUrl(apiInfo.getApiServerUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Company-Id", apiInfo.getCompanyId())
                // 현재 사용자 정보를 헤더로 전달
                .defaultHeaders(headers -> {
                    try {
                        SecUser currentUser = SecurityUtils.getCurrentUser();
                        if (currentUser != null && currentUser.getUserId() != null) {
                            // 사용자 정보를 커스텀 헤더로 전달
                            headers.set("X-User-Id", currentUser.getUserId());
                            headers.set("X-Login-Id", currentUser.getLgnId());
                            // 헤더는 ISO-8859-1 만 안전하다. 한글 이름을 그대로 실으면 수신 측에서 깨지고
                            // 처리 중 실패한다(desk 실측 500). ASCII 로 인코딩해 보내고 수신 측에서 복원한다.
                            headers.set("X-User-Name", InternalHeaderValue.encode(currentUser.getUserNm()));
                            if (currentUser.getOgnzCd() != null) {
                                headers.set("X-Org-Code", InternalHeaderValue.encode(currentUser.getOgnzCd()));
                            }
                            // 권한 헤더 전파 (09b (C) 항목11). desk 관리 엔드포인트(/api/admin/**)는 관리자 컨텍스트이므로
                            // ROLE_ADM 을 포함(admin 백오피스가 desk 에 대해 관리자로 동작) + 사용자 실제 권한은 감사·세분화용.
                            java.util.LinkedHashSet<String> deskAuths = new java.util.LinkedHashSet<>();
                            deskAuths.add("ROLE_ADM");
                            java.util.List<String> userAuths = currentUser.getAuthrtCdList();
                            if ((userAuths == null || userAuths.isEmpty()) && currentUser.getAuthrtCd() != null) {
                                userAuths = java.util.Collections.singletonList(currentUser.getAuthrtCd());
                            }
                            if (userAuths != null) {
                                deskAuths.addAll(userAuths);
                            }
                            headers.set("X-User-Authorities", String.join(",", deskAuths));
                            log.debug("desk 헤더 전달 - userId: {}, authorities: {}, companyId: {}",
                                    currentUser.getUserId(), deskAuths, apiInfo.getCompanyId());
                        } else {
                            log.warn("현재 사용자 정보가 없습니다 - companyId: {}", apiInfo.getCompanyId());
                        }
                    } catch (Exception e) {
                        log.error("사용자 정보 추출 중 오류 - companyId: {}", apiInfo.getCompanyId(), e);
                    }
                    
                    // desk 공유 내부키 전송(회원사별 토큰이 아닌 공유키) — desk AdminApiAuthFilter 가 검증 (09b (C) 항목10)
                    // 회원사별 api_auth_token 은 전부 NULL 이었고 desk 는 공유키만 검증하므로 공유키로 세팅.
                    headers.set("X-Api-Key", (outboundApiKey != null && !outboundApiKey.isBlank()) ? outboundApiKey : "local-api-key");
                    // 회원사별 인증이 실제로 필요해지면 별도 헤더로 분리(현재 desk 미검증)
                    if (apiInfo.getApiAuthToken() != null && !apiInfo.getApiAuthToken().isBlank()) {
                        headers.set("X-Company-Api-Token", apiInfo.getApiAuthToken());
                    }
                })
                .build();
    }
    
    
    /**
     * 에러 핸들링
     * 
     * @param throwable 발생한 예외
     * @return 변환된 예외
     */
    private Throwable handleError(Throwable throwable) {
        if (throwable instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) throwable;
            log.error("회원사 API 응답 오류 - statusCode: {}, responseBody: {}", 
                    ex.getStatusCode(), ex.getResponseBodyAsString());
            return ex;
        }
        return throwable;
    }
    
    /**
     * 예외를 CompanyApiException으로 변환
     * 
     * @param companyId 회원사ID
     * @param endpoint API 엔드포인트
     * @param exception 발생한 예외
     * @return CompanyApiException
     */
    private CompanyApiException convertToCompanyApiException(String companyId, String endpoint, Exception exception) {
        if (exception instanceof CompanyApiException) {
            return (CompanyApiException) exception;
        }
        
        if (exception instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) exception;
            String errorMessage = extractErrorMessage(ex);
            return new CompanyApiException(
                    companyId,
                    endpoint,
                    ex.getStatusCode().value(),
                    errorMessage,
                    ex
            );
        }

        // 연결 거부/타임아웃 등 네트워크 계열(회원사 API 서버 미기동 포함) → 서버 상태 점검 오류(503)
        // 일반 "시스템 오류"와 구분하여 사용자가 서버 상태 문제임을 인지하도록 함
        if (isServerUnavailable(exception)) {
            log.error("회원사 API 서버 연결 실패(서버 상태 점검 필요) - endpoint: {}", endpoint, exception);
            return new CompanyApiException(
                    companyId,
                    endpoint,
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "회원사 API 서버에 연결할 수 없습니다. [기본정보] 탭의 '대상 desk 서버' 선택을 확인하거나, 서버 카탈로그(SVR0010)에서 해당 서버의 주소·상태를 점검해 주세요.",
                    exception
            );
        }

        return new CompanyApiException(
                companyId,
                endpoint,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getMessage() != null ? exception.getMessage() : "알 수 없는 오류가 발생했습니다.",
                exception
        );
    }

    /**
     * 연결 거부/타임아웃 등 서버 미기동·네트워크 계열 오류 여부 판정 (원인 체인 탐색)
     *
     * @param throwable 발생한 예외
     * @return 서버 상태(연결 불가/타임아웃) 계열이면 true
     */
    private boolean isServerUnavailable(Throwable throwable) {
        Throwable t = throwable;
        while (t != null) {
            if (t instanceof org.springframework.web.reactive.function.client.WebClientRequestException
                    || t instanceof java.net.ConnectException
                    || t instanceof java.net.UnknownHostException
                    || t instanceof java.util.concurrent.TimeoutException) {
                return true;
            }
            String name = t.getClass().getName();
            if (name.contains("ConnectTimeout") || name.contains("ReadTimeout")
                    || name.contains("ConnectException")) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
    
    /**
     * WebClientResponseException에서 오류 메시지 추출
     * 
     * @param ex WebClientResponseException
     * @return 추출된 오류 메시지
     */
    private String extractErrorMessage(WebClientResponseException ex) {
        String responseBody = ex.getResponseBodyAsString();
        
        // 응답 본문이 있는 경우 JSON 파싱 시도
        if (responseBody != null && !responseBody.isEmpty()) {
            try {
                // JSON 응답에서 message 필드 추출 시도
                if (responseBody.contains("\"message\"")) {
                    int startIdx = responseBody.indexOf("\"message\"");
                    int colonIdx = responseBody.indexOf(":", startIdx);
                    int valueStart = responseBody.indexOf("\"", colonIdx) + 1;
                    int valueEnd = responseBody.indexOf("\"", valueStart);
                    if (valueEnd > valueStart) {
                        return responseBody.substring(valueStart, valueEnd);
                    }
                }
                // JSON 파싱 실패 시 전체 응답 반환 (최대 200자)
                return responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." : responseBody;
            } catch (Exception e) {
                log.warn("응답 본문 파싱 실패", e);
            }
        }
        
        // 기본 메시지 반환
        return String.format("HTTP %d: %s", ex.getStatusCode().value(), ex.getStatusText());
    }
}
