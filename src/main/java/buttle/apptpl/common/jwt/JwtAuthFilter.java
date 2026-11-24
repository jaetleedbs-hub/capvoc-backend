package buttle.apptpl.common.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.util.CoreUtils;


@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 이미 인증된 경우 JWT 검사 스킵 (AICC API 요청 등)
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.debug("Already authenticated, skipping JWT validation for URI: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 검사를 예외로 처리할 URL 목록
        List<String> excludedUrls = List.of(
            "/login"
            , "/error"
            , "/jsonView"
            , "/redirect"
            , "/api/auth/"
            // [§10.89] "/api/external/" 을 뺐다.
            //  이 경로는 <JWT 도 인가도> 건너뛰는 유일한 업무 경로였는데 컨트롤러가 하나도 없었다.
            //  그런데 DEVELOPMENT_GUIDE 가 바로 이 경로에 컨트롤러를 만들라고 예시하고 있어,
            //  가이드를 따르는 순간 <인증도 인가도 없는 API> 가 생기고 만든 사람은 알 방법이 없다.
            //  dev 실측: 다른 경로는 인가 관리자에 닿아 AUDIT 로그가 남는데 이 경로만 로그가 없다
            //  (= 검사 자체를 안 한다). 되살릴 일이 있으면 키 인증을 함께 붙일 것.
            , "/api/common/user-auth" // 중복로그인 체크 外
            , "/api/com0020/u01" // 비밀번호 변경 
            
            , "/-/metrics"
            , "/api-docs"
            , "/v3/api-docs"  // Swagger 3.0 API 문서
            , "/swagger-ui/"
            , "/swagger-resources/"
            , "/favicon.ico"
        );

        // 현재 요청 URI가 예외 처리 목록에 포함되어 있는지 확인
        for (String url : excludedUrls) {
            if (requestURI.startsWith(url)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        try{
            if( log.isDebugEnabled()){
                log.debug("Processing JWT authentication for URI: {}", requestURI);
            }
            
            // 인증 정보 설정
            Authentication authentication = jwtService.getAuthentication(request, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.info("JWT authentication successful for URI: {}, User: {}", requestURI, authentication != null ? authentication.getName() : "anonymous");

            filterChain.doFilter(request, response);
        } catch (JwtTokenException e) {
            if( e.getErrorCode() == 471){
                log.debug("JwtTokenException for URI: {} - Code: {}, Message: {}", requestURI, e.getErrorCode(), e.getDetailMessage());
            } else {
                log.warn("JwtTokenException for URI: {} - Code: {}, Message: {}, remote: {}", requestURI, e.getErrorCode(), e.getDetailMessage(), CoreUtils.webutils.getRemoteIp());
            }

            String errorMsg = "{\"error\": \"Invalid\",\"message\": \"%s\",\"status\": %d,\"errors\": []}";
            errorMsg = String.format(errorMsg, e.getDetailMessage(), e.getErrorCode());
            response.setStatus(401);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().print(errorMsg);
            response.flushBuffer();
        }
    }


}