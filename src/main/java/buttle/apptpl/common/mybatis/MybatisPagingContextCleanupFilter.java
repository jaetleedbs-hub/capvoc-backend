package buttle.apptpl.common.mybatis;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP 요청 종료 시 MyBatis Paging Context를 정리하는 Filter
 * 
 * <pre>
 * ThreadLocal 메모리 누수를 방지하기 위해 요청이 완전히 종료된 후 
 * MybatisPagingContext를 명시적으로 정리합니다.
 * 
 * 참고: MybatisPrepareInterceptor에서도 각 쿼리 실행 전에 clearContext()를 호출하므로
 * 이 Filter는 추가 안전장치 역할을 합니다.
 * </pre>
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE) // 가장 마지막에 실행되도록
public class MybatisPagingContextCleanupFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 요청 처리 완료 후 반드시 Context 정리
            MybatisPagingContextHandler.clearContext();
            log.trace("Cleaned up MybatisPagingContext after request: {} {}", request.getMethod(), request.getRequestURI());
        }
    }
}

