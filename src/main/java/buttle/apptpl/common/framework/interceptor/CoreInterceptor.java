package buttle.apptpl.common.framework.interceptor;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;
import buttle.apptpl.common.framework.config.EnvConfig;
import micoai.corebase.util.CoreUtils.string;
import micoai.corebase.util.CoreUtils.webutils;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreInterceptor implements HandlerInterceptor {
	private final EnvConfig config;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		checkCsrf(request);

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String queryString = request.getQueryString();
        String httpMethod = string.upperCase(request.getMethod());
        SecUser member = SecurityUtils.getCurrentUser();

        request.setAttribute("contextPath", contextPath);
        if (member != null) {
        	request.setAttribute("_currentUser", member);
        }
        else {
        	member = SecurityUtils.getAnonymousUser();
        }

        if (string.isNotBlank(contextPath)) {
            requestURI = requestURI.substring(contextPath.length());
        }

    	String remoteIp = webutils.getRemoteIp(request);
        log.info("===============================================================");
        log.info("   Remote IP: " + remoteIp);
        log.info(" HTTP Method: " + httpMethod);
        log.info(" ContextPath: " + contextPath);
        log.info("  RequestURI: " + requestURI);
        log.info(" QueryString: " + maskSensitiveQuery(queryString));
        log.info("---------------------------------------------------------------");
    	log.info("    memberId: " + member.getUserId());
        log.info("     loginId: " + member.getLgnId());
        log.info("    memberNm: " + member.getUserNm());
        log.info("       roles: " + member.getAuthrtCdList());
        log.info("===============================================================");

		return true;
	}

	/**
	 * 쿼리스트링에서 민감 파라미터의 값을 가린다 [N-32].
	 *
	 * <p>corebase 의 동명 인터셉터에서 조치한 것과 같은 결함이다. admin-backend 는
	 * corebase 것을 쓰지 않고 <b>자체 복사본</b>을 갖고 있어 그 조치가 적용되지 않았다.
	 * 쿼리스트링 전체를 INFO 레벨로 기록하므로 로그 레벨을 낮춰도 사라지지 않는다.
	 */
	private String maskSensitiveQuery(String queryString) {
		if (string.isBlank(queryString)) {
			return queryString;
		}

		StringBuilder masked = new StringBuilder(queryString.length());
		for (String pair : queryString.split("&")) {
			if (masked.length() > 0) {
				masked.append('&');
			}
			int eq = pair.indexOf('=');
			if (eq < 0) {
				masked.append(pair);
				continue;
			}
			String name = pair.substring(0, eq);
			masked.append(name).append('=');
			masked.append(isSensitiveParam(name) ? "***" : pair.substring(eq + 1));
		}
		return masked.toString();
	}

	/** 값이 그대로 노출되면 안 되는 파라미터명인지 판단한다. */
	private boolean isSensitiveParam(String name) {
		String lower = string.lowerCase(string.trim(name));
		// t: SSO 단축토큰(/sl?t=), token/pswd 계열: 자격증명
		return "t".equals(lower)
				|| lower.contains("token")
				|| lower.contains("pswd")
				|| lower.contains("password")
				|| lower.contains("secret")
				|| lower.contains("apikey")
				|| lower.contains("api-key");
	}

	/** CSRF 비활성 상태에서 화면이 참조하는 대체 토큰을 보관할 세션 키 */
	private static final String FALLBACK_CSRF_SESSION_KEY = "_csrf_fallback_token";

	/**
	 * Spring Security의 CsrfFilter가 토큰을 설정하지 않은 경우(= CSRF 비활성),
	 * 화면 렌더링이 깨지지 않도록 대체 토큰을 request에 넣어준다.
	 *
	 * <p>과거에는 전 사용자·전 인스턴스가 공유하는 <b>고정 상수</b>
	 * ({@code 9f969537-…})를 주입했다. 상수값을 아는 공격자는 유효한 토큰을 그대로
	 * 붙일 수 있어 CSRF 방어가 무력화된다 [N-03 / W0-6].
	 *
	 * <p>corebase 에서 조치한 것과 <b>같은 상수·같은 결함</b>이었으나, admin 은 자체
	 * 복사본을 쓰고 있어 그 조치가 적용되지 않았다. 여기서도 <b>세션별 난수</b>로 발급한다.
	 *
	 * <p>주의: 이 토큰은 서버가 검증하지 않는 <b>렌더링용 대체값</b>일 뿐 CSRF 방어가 아니다.
	 * 실제 방어는 CSRF를 활성화해 Spring Security가 발급·검증하게 해야 한다.
	 */
	private void checkCsrf(HttpServletRequest request) {
		CsrfToken csrf = (CsrfToken)request.getAttribute("_csrf");
		if (csrf != null) {
			return;
		}

		HttpSession session = request.getSession(true);
		String token = (String)session.getAttribute(FALLBACK_CSRF_SESSION_KEY);
		if (token == null) {
			token = UUID.randomUUID().toString();
			session.setAttribute(FALLBACK_CSRF_SESSION_KEY, token);
			log.warn("CSRF가 비활성 상태입니다. 렌더링용 대체 토큰을 발급합니다. 운영에서는 CSRF를 활성화하십시오.");
		}

		request.setAttribute("_csrf", new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", token));
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

	}


}
