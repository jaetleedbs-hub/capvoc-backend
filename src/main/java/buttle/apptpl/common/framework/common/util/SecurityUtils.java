package buttle.apptpl.common.framework.common.util;

import jakarta.servlet.http.HttpSession;
import buttle.apptpl.common.framework.common.vo.CustomUserDetails;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.exception.UnauthorizedException;
import micoai.corebase.util.CoreUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

public class SecurityUtils {
	public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
	private static final List<String> ANONYMOUS_ROLES = Arrays.asList(ROLE_ANONYMOUS);

	private SecurityUtils() {

	}

	/**
	 * 현재 사용자 기본 정보 조회
	 *
	 * @return 회원정보
	 */
	public static SecUser getCurrentUser() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getPrincipal() == null) {
			return getAnonymousUser();
		}

		Object user = authentication.getPrincipal();
		if (user instanceof CustomUserDetails) {
			return ((CustomUserDetails) user).getSecUser();
		} else if (user instanceof SecUser) {
			return (SecUser) user;
		} else {
			return getAnonymousUser();
		}
	}

	public static String getCurrentUserId() {
		return getCurrentUser().getUserId();
	}

	public static String getCurrentUserOgnzCd() {
		return getCurrentUser().getOgnzCd();
	}

	public static SecUser getSecMbrFromAuthentication(Authentication authentication) {
		if (authentication == null || authentication.getPrincipal() == null) {
			return getAnonymousUser();
		}

		Object user = authentication.getPrincipal();
		if (user instanceof CustomUserDetails) {
			return ((CustomUserDetails) user).getSecUser();
		} else if (user instanceof SecUser) {
			return (SecUser) user;
		} else {
			return getAnonymousUser();
		}
	}

	private static String getSessionId() {
		HttpSession session = CoreUtils.session.getSession(true);
		if (session == null) {
			return "no-session-id";
		}
		return session.getId();
	}
	/**
	 * 로그인하지 않은 경우의 사용자 정보
	 *
	 * @return 회원정보
	 */
	public static SecUser getAnonymousUser() {
		String sessionId = getSessionId();
		if (sessionId == null) {
			sessionId = "no-session-id";
		}
		String lgnId = "sid-" + sessionId;
		SecUser member = SecUser.builder()
				.userId(lgnId)
				.lgnId(lgnId)
				.userNm("anonymousUser")
				.authrtCdList(ANONYMOUS_ROLES)
				.enabled(false)
				.build();
		return member;
	}

	/**
	 * 로그인 여부 검사
	 * @return 회원정보
	 */
	public static SecUser checkLogin() {
		SecUser worker = getCurrentUser();
		if (worker == null) {
			throw new UnauthorizedException();
		}

		return worker;
	}

	/*
	 * Security session
	 */
	public static class session {
		public static final String SESSION_ID = "-force-login-pswd-";

		/**
		 * kakao, naver, google, sso 등 강제로그인에 사용할 임시비밀번호 얻기
		 * @return
		 */
		public static String getForceLoginPassword() {
			HttpSession ss = CoreUtils.session.getSession(true);
			String pswd = (String)ss.getAttribute(SESSION_ID);
			return pswd;
		}

		/**
		 * kakao, naver, google, sso 등 강제로그인에 사용할 임시비밀번호 설정
		 * @param pswd 임시비밀번호
		 */
		public static void setForceLoginPassword(String pswd) {
			HttpSession ss = CoreUtils.session.getSession(true);
			ss.setAttribute(SESSION_ID, pswd);
		}

		/**
		 * 임시비밀번호 삭제
		 */
		public static void removeForceLoginPassword() {
			HttpSession ss = CoreUtils.session.getSession(true);
			ss.removeAttribute(SESSION_ID);
		}
	}
}
