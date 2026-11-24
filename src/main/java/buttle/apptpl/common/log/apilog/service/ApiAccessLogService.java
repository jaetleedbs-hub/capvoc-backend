package buttle.apptpl.common.log.apilog.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.util.CoreUtils;
import buttle.apptpl.common.log.config.LogConfig;
import buttle.apptpl.common.log.personal.PersonalAccessLogService;
import buttle.apptpl.common.model.logt.entity.LogtAccessLogEntity;
import buttle.apptpl.common.model.logt.repository.LogtAccessLogRepo;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j // 로깅을 위한 Lombok 어노테이션
public class ApiAccessLogService {

    private final LogtAccessLogRepo logtAccessLogRepo;
    private final PersonalAccessLogService personalAccessLogService;
    private final LogConfig logConfig;

    /** 시스템 코드. admin 접속로그의 sys_cd. */
    @Value("${env.sys-cd:admin}")
    private String sysCd;



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(RequestData requestData, Object responseData) {

        if( !logConfig.isUseApiLog()) {
            return;
        }

        try {
            // 입력 데이터 유효성 검증
            if (requestData == null) {
                log.warn("RequestData is null, skipping log save");
                return;
            }

            // [N-18 되돌림] admin 자기 신원(user_id/user_nm)으로 기록. log_id 는 앱이 생성해
            // 개인정보 접근이력의 부모키로 재사용한다.
            String logId = CoreUtils.string.getNewId("alog-");
            LogtAccessLogEntity logEntity = LogtAccessLogEntity.builder()
                .logId(logId)
                .sysCd(sysCd)
                .logDt(LocalDateTime.now())
                .httpMethod(requestData.getMethod())
                .urlAddr(requestData.getRequestURI())
                .userId(requestData.getUserId())
                .ipAddr(requestData.getIpAddr())
                .build();

            logtAccessLogRepo.save(logEntity);

            // 개인정보 로그 처리 — 부모키는 log_id, 등록자는 admin user_id
            if( logConfig.isUsePersonalLog()) {
                personalAccessLogService.processAndSavePersonalLogs(responseData, logId, requestData.getUserId());
            }
            
        } catch (org.springframework.dao.DataAccessException | org.springframework.transaction.TransactionException e) {
            // 데이터베이스/트랜잭션 관련 오류
            log.error("API 로그 저장 실패 - 데이터베이스/트랜잭션 오류: {}", e.getMessage());
            log.debug("RequestData: {}", requestData);
        } catch (IllegalArgumentException e) {
            // 입력값 검증 오류
            log.error("API 로그 저장 실패 - 잘못된 입력값: {}", e.getMessage());
            log.debug("RequestData: {}", requestData);
        } catch (Exception e) {
            // 예상치 못한 시스템 오류
            log.error("API 로그 저장 실패 - 예상치 못한 오류: {}", e.getMessage(), e);
        }
    }

    public static RequestData extractRequestData(HttpServletRequest request) {
        // 입력 유효성 검증
        if (request == null) {
            log.warn("HttpServletRequest is null, returning default RequestData");
            return createDefaultRequestData("NULL_REQUEST");
        }

        try {
            // [N-18 되돌림] admin 신원(user_id/user_nm). 미인증 요청이면 null(익명 접근도 기록).
            SecUser currentUser = SecurityUtils.getCurrentUser();
            return RequestData.builder()
                .requestURI(request.getRequestURI())
                .method(request.getMethod())
                .userId(currentUser != null ? currentUser.getUserId() : null)
                .ipAddr(CoreUtils.webutils.getRemoteIp(request))
                .build();

        } catch (IllegalStateException | UnsupportedOperationException | IllegalArgumentException e) {
            // 요청 객체 상태 오류 (재활용, 지원되지 않는 작업 등)
            log.warn("Request 데이터 추출 실패 - 요청 상태 오류: {}", e.getMessage());
            return createDefaultRequestData("REQUEST_STATE_ERROR");
            
        } catch (SecurityException e) {
            // 보안 컨텍스트 오류 (사용자 ID 조회 실패 등)
            log.warn("Request 데이터 추출 실패 - 보안 컨텍스트 오류: {}", e.getMessage());
            return RequestData.builder()
                .requestURI(safeGetRequestURI(request))
                .method(safeGetMethod(request))
                .userId("SECURITY_ERROR")
                .ipAddr(safeGetRemoteIp(request))
                .build();
            
        } catch (Exception e) {
            // 예상치 못한 시스템 오류
            log.warn("Request 데이터 추출 실패 - 예상치 못한 오류: {}", e.getMessage(), e);
            return createDefaultRequestData("UNEXPECTED_ERROR");
        }
    }

    /**
     * 기본 RequestData 생성
     */
    private static RequestData createDefaultRequestData(String errorType) {
        return RequestData.builder()
            .requestURI(errorType)
            .method(errorType)
            .userId("UNKNOWN")
            .ipAddr(errorType)
            .build();
    }

    /**
     * 안전한 RequestURI 조회
     */
    private static String safeGetRequestURI(HttpServletRequest request) {
        try {
            return request != null ? request.getRequestURI() : "NULL_REQUEST";
        } catch (IllegalStateException | UnsupportedOperationException e) {
            // 요청 객체 상태 오류
            log.debug("RequestURI 조회 실패 - 요청 상태 오류: {}", e.getMessage());
            return "URI_STATE_ERROR";
        } catch (IllegalArgumentException | NullPointerException e) {
            // 입력값 오류
            log.debug("RequestURI 조회 실패 - 입력값 오류: {}", e.getMessage());
            return "URI_INPUT_ERROR";
        } catch (Exception e) {
            // 예상치 못한 시스템 오류
            log.debug("RequestURI 조회 실패 - 예상치 못한 오류: {}", e.getMessage());
            return "URI_ERROR";
        }
    }

    /**
     * 안전한 HTTP Method 조회
     */
    private static String safeGetMethod(HttpServletRequest request) {
        try {
            return request != null ? request.getMethod() : "NULL_REQUEST";
        } catch (IllegalStateException | UnsupportedOperationException e) {
            // 요청 객체 상태 오류
            log.debug("HTTP Method 조회 실패 - 요청 상태 오류: {}", e.getMessage());
            return "METHOD_STATE_ERROR";
        } catch (IllegalArgumentException | NullPointerException e) {
            // 입력값 오류
            log.debug("HTTP Method 조회 실패 - 입력값 오류: {}", e.getMessage());
            return "METHOD_INPUT_ERROR";
        } catch (Exception e) {
            // 예상치 못한 시스템 오류
            log.debug("HTTP Method 조회 실패 - 예상치 못한 오류: {}", e.getMessage());
            return "METHOD_ERROR";
        }
    }

    /**
     * 안전한 Remote IP 조회
     */
    private static String safeGetRemoteIp(HttpServletRequest request) {
        try {
            return request != null ? CoreUtils.webutils.getRemoteIp(request) : "NULL_REQUEST";
        } catch (IllegalStateException | UnsupportedOperationException e) {
            // 요청 객체 상태 오류
            log.debug("Remote IP 조회 실패 - 요청 상태 오류: {}", e.getMessage());
            return "IP_STATE_ERROR";
        } catch (IllegalArgumentException | NullPointerException e) {
            // 입력값 오류
            log.debug("Remote IP 조회 실패 - 입력값 오류: {}", e.getMessage());
            return "IP_INPUT_ERROR";
        } catch (Exception e) {
            // 예상치 못한 시스템 오류
            log.debug("Remote IP 조회 실패 - 예상치 못한 오류: {}", e.getMessage());
            return "IP_ERROR";
        }
    }

    @lombok.Builder
    @lombok.Data
    public static class RequestData {
        private String requestURI;
        private String method;
        private String ipAddr;
        // [N-18 되돌림] admin 신원. 이름은 담지 않는다 [PII-4, 2026-08-31] —
        // 조회 화면은 usr_id 로 cdm_own.usr 를 조인해 이름을 얻는다.
        private String userId;
    }
}