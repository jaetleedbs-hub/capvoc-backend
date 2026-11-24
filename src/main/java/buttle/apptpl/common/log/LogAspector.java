package buttle.apptpl.common.log;



import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.log.apilog.service.ApiAccessLogService;



@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspector {
    
    private final ApiAccessLogService apiAccessLogService;

    @SuppressWarnings("resource")
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object applySecurityMasking(ProceedingJoinPoint joinPoint) throws Throwable {
        
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestURI = request.getRequestURI();
        
        // API가 아닌 경우 로그 처리 제외
        if (!requestURI.startsWith("/api") || requestURI.startsWith("/api/common/polling")) {
            return joinPoint.proceed();
        }
        
        // 비동기 처리를 위해 Request 데이터를 미리 추출 (Request 객체 재활용 방지)
        ApiAccessLogService.RequestData requestData = null;
        try {
            requestData = ApiAccessLogService.extractRequestData(request);
            
        } catch (IllegalStateException e) {
            // Request 상태가 올바르지 않은 경우 (이미 처리된 요청 등)
            log.warn("Request 상태 오류로 데이터 추출 실패: {}", e.getMessage());
            
        } catch (IllegalArgumentException e) {
            // 잘못된 Request 파라미터
            log.warn("잘못된 Request 파라미터로 데이터 추출 실패: {}", e.getMessage());
            
        } catch (NullPointerException e) {
            // Request 객체나 필수 속성이 null인 경우
            log.warn("Request 객체 null로 인한 데이터 추출 실패: {}", e.getMessage());
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("Request 데이터 추출 중 예상치 못한 오류: {}", e.getMessage(), e);
        }
        
        final Map<String, Object> resultMap = new HashMap<>();
        Object result = null;
        try{
            // 컨트롤러 메서드 실행
            result = joinPoint.proceed();
            resultMap.put("result", result);
        }finally{
            // Request 데이터가 성공적으로 추출된 경우에만 로그 저장
            if (requestData != null) {
                try{
                    final ApiAccessLogService.RequestData finalRequestData = requestData;
                    TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
                    taskExecutor.execute(() -> {
                        try{
                            apiAccessLogService.saveLog(finalRequestData, resultMap.get("result"));
                            resultMap.clear();
                            
                        } catch (IllegalArgumentException e) {
                            // 잘못된 로그 데이터 형식
                            log.warn("잘못된 로그 데이터로 인한 저장 실패: {}", e.getMessage());
                            
                        } catch (NullPointerException e) {
                            // 필수 로그 데이터가 null인 경우
                            log.warn("필수 로그 데이터 null로 인한 저장 실패: {}", e.getMessage());
                            
                        } catch (RuntimeException e) {
                            // 데이터베이스 연결 오류 등 런타임 예외
                            log.error("로그 저장 중 런타임 오류: {}", e.getMessage(), e);
                            
                        } catch (Exception e) {
                            // 기타 예상치 못한 오류
                            log.error("API 접속 로그 저장 중 예상치 못한 오류: {}", e.getMessage(), e);
                        }
                    });
                    
                } catch (IllegalStateException e) {
                    // TaskExecutor 상태 오류
                    log.warn("TaskExecutor 상태 오류로 비동기 처리 실패: {}", e.getMessage());
                    
                } catch (SecurityException e) {
                    // 보안 정책으로 인한 스레드 생성 실패
                    log.error("보안 정책으로 인한 비동기 처리 실패: {}", e.getMessage());
                    
                } catch (Exception e) {
                    // 기타 비동기 처리 오류
                    log.error("API 접속 로그 비동기 처리 중 예상치 못한 오류: {}", e.getMessage(), e);
                }
            }
        }
        
        return result;
    }


}
