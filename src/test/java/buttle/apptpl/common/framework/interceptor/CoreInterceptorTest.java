package buttle.apptpl.common.framework.interceptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * admin-backend 자체 {@code CoreInterceptor} 의 회귀 테스트.
 *
 * <p><b>왜 별도로 필요한가</b> — 같은 결함을 corebase 에서 조치했으나(W0-6, N-32),
 * admin 은 corebase 의 인터셉터를 쓰지 않고 <b>자체 복사본</b>을 갖고 있어 그 조치가
 * 전혀 적용되지 않았다. 실기동 로그에서 admin 의 인터셉터가 별도로 동작하는 것을
 * 확인해 발견했다(N-37). 복사본이 다시 갈라지지 않도록 여기서 고정한다.
 */
class CoreInterceptorTest {

	/** 과거 하드코딩되어 있던 값. 어떤 경로로도 다시 나타나면 안 된다. */
	private static final String LEAKED_CONSTANT = "9f969537-8c4d-488d-9c69-7b2bddc181ab";

	private CoreInterceptor interceptor() {
		// EnvConfig 는 preHandle 에서만 쓰이므로 아래 검증 대상 메서드에는 영향이 없다.
		return new CoreInterceptor(null);
	}

	private String mask(String queryString) {
		return ReflectionTestUtils.invokeMethod(interceptor(), "maskSensitiveQuery", queryString);
	}

	private String issueToken(MockHttpSession session) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);

		ReflectionTestUtils.invokeMethod(interceptor(), "checkCsrf", request);

		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		assertNotNull(token, "대체 토큰이 발급되어야 한다");
		return token.getToken();
	}

	// ── W0-6 하드코딩 CSRF 토큰 제거 ──────────────────────────────────────────

	@Test
	@DisplayName("[W0-6] 하드코딩 상수가 더 이상 발급되지 않는다")
	void doesNotIssueHardcodedConstant() {
		assertNotEquals(LEAKED_CONSTANT, issueToken(new MockHttpSession()));
	}

	@Test
	@DisplayName("[W0-6] 세션마다 서로 다른 토큰이 발급된다 — 타 사용자 토큰 재사용 차단")
	void issuesDistinctTokenPerSession() {
		assertNotEquals(issueToken(new MockHttpSession()), issueToken(new MockHttpSession()));
	}

	@Test
	@DisplayName("[W0-6] 같은 세션에서는 동일 토큰이 유지된다 — 화면 렌더링 일관성")
	void keepsSameTokenWithinSession() {
		MockHttpSession session = new MockHttpSession();
		assertEquals(issueToken(session), issueToken(session));
	}

	@Test
	@DisplayName("[W0-6] Spring Security가 이미 발급한 토큰은 덮어쓰지 않는다")
	void doesNotOverrideExistingToken() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(new MockHttpSession());
		CsrfToken existing = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "issued-by-spring-security");
		request.setAttribute("_csrf", existing);

		ReflectionTestUtils.invokeMethod(interceptor(), "checkCsrf", request);

		assertEquals(existing, request.getAttribute("_csrf"));
	}

	// ── N-32 쿼리스트링 자격증명 마스킹 ────────────────────────────────────────

	@Test
	@DisplayName("[N-32] SSO 단축토큰 파라미터 t 의 값이 가려진다")
	void masksSsoShortToken() {
		assertEquals("t=***", mask("t=0x989kqv8VCFrQJRGXnZPv"));
	}

	@Test
	@DisplayName("[N-32] token·password·secret·apiKey 계열이 가려진다")
	void masksCredentialLikeParams() {
		assertEquals("accessToken=***", mask("accessToken=ey.header.payload"));
		assertEquals("userPswd=***", mask("userPswd=1234"));
		assertEquals("password=***", mask("password=1234"));
		assertEquals("client_secret=***", mask("client_secret=abcd"));
		assertEquals("apiKey=***", mask("apiKey=abcd"));
	}

	@Test
	@DisplayName("[N-32] 파라미터명 대소문자에 관계없이 가려진다")
	void masksRegardlessOfCase() {
		assertEquals("T=***", mask("T=abcd"));
		assertEquals("ACCESSTOKEN=***", mask("ACCESSTOKEN=abcd"));
	}

	@Test
	@DisplayName("[N-32] 비민감 파라미터는 그대로 남아 추적성을 유지한다")
	void keepsNonSensitiveParams() {
		assertEquals("page=2&size=20", mask("page=2&size=20"));
		assertEquals("t=***&page=2&token=***&size=20", mask("t=abc&page=2&token=xyz&size=20"));
	}

	@Test
	@DisplayName("[N-32] 값이 없는 조각과 빈 입력을 그대로 통과시킨다")
	void handlesEdgeCases() {
		assertNull(mask(null));
		assertEquals("", mask(""));
		assertEquals("flagOnly", mask("flagOnly"));
		assertEquals("t=***", mask("t="));
	}
}
