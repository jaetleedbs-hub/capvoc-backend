package buttle.apptpl.common.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 「지금 언어는 무엇인가」의 판정 규칙을 못박는다.
 *
 * <p>이 규칙이 무너지는 방식은 조용하다 — 한국어 화면의 콤보에 영어가 섞여 뜨거나,
 * 배치가 서버 OS 의 언어로 코드명을 읽는다. 오류가 나지 않으므로 시험이 지켜야 한다.
 */
class LocaleSupportTest {

	private final LocaleSupport support = new LocaleSupport("ko", "ko, en");

	@AfterEach
	void tearDown() {
		RequestContextHolder.resetRequestAttributes();
		LocaleContextHolder.resetLocaleContext();
	}

	@Test
	@DisplayName("요청의 언어가 지원 목록에 있으면 그것을 쓴다")
	void usesRequestLocaleWhenSupported() {
		inRequestWith(Locale.ENGLISH);
		assertEquals("en", support.current());
	}

	@Test
	@DisplayName("요청의 언어가 지원 목록에 없으면 기본값으로 떨어진다 — 아무 언어나 받지 않는다")
	void fallsBackWhenUnsupported() {
		inRequestWith(Locale.JAPANESE);
		assertEquals("ko", support.current());
	}

	@Test
	@DisplayName("요청이 없으면(배치·부팅) 설정의 기본값이다 — JVM 기본 로케일에 기대지 않는다")
	void usesConfiguredDefaultOutsideRequest() {
		Locale saved = Locale.getDefault();
		try {
			Locale.setDefault(Locale.JAPANESE);   // 서버 OS 가 일본어여도
			assertEquals("ko", support.current());
		} finally {
			Locale.setDefault(saved);
		}
	}

	@Test
	@DisplayName("지원 목록은 공백을 허용한다 — 'ko, en' 도 'ko,en' 과 같다")
	void trimsSupportedList() {
		inRequestWith(Locale.ENGLISH);
		assertEquals("en", new LocaleSupport("ko", " ko , en ").current());
	}

	private static void inRequestWith(Locale locale) {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
		LocaleContextHolder.setLocale(locale);
	}
}
