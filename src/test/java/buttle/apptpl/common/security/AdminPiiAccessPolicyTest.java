package buttle.apptpl.common.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.pii.PiiContext;
import micoai.corebase.pii.PiiGrade;
import micoai.corebase.pii.PiiType;

/**
 * admin 정책 — <b>고객 등급은 항상 마스킹(안전망), 임직원 등급은 인증 시 원본</b>.
 *
 * <p>지금 admin 에는 고객 데이터를 조회하는 화면이 없어 실제로 가려지는 것이 없다. 이 테스트가 고정하는
 * 것은 <b>나중에 고객 데이터가 들어왔을 때의 기본값</b>이다 — 기본이 "보인다"이면 그때 아무도 모르게 노출된다.
 */
class AdminPiiAccessPolicyTest {

	private AdminPiiAccessPolicy policy() {
		AdminPiiAccessPolicy p = new AdminPiiAccessPolicy();
		p.register();
		return p;
	}

	private void loginAsAdmin() {
		SecUser user = SecUser.builder().userId("u-1").lgnId("demo01").userNm("관리자").build();
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, "", List.of()));
	}

	/**
	 * {@link PiiContext} 는 <b>정적 홀더</b>라 같은 JVM 의 다른 테스트에 그대로 전달된다.
	 * 여기서 "전부 거부" 람다로 되돌리면 {@code PiiRevealIntegrationTest}(스프링 컨텍스트가 등록한
	 * 실제 정책을 기대한다)가 <b>403 으로 깨진다</b> — 실제로 겪었다.
	 * 그래서 앱이 기동 시 등록하는 것과 <b>같은 정책</b>으로 되돌린다.
	 */
	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
		new AdminPiiAccessPolicy().register();
	}

	@Test
	@DisplayName("고객 등급은 인증된 관리자에게도 마스킹된다 — 운영 콘솔은 취급자가 아니다")
	void customerGradeAlwaysMasked() {
		AdminPiiAccessPolicy p = policy();
		loginAsAdmin();

		assertFalse(p.canViewOriginal(PiiType.MOBILE, PiiGrade.CUSTOMER));
		assertFalse(p.canViewOriginal(PiiType.NAME), "등급 미지정 기본값(CUSTOMER)도 마스킹");
	}

	@Test
	@DisplayName("임직원 등급은 인증 시 원본 — 계정·조직 관리가 성립해야 한다")
	void staffGradeVisibleWhenAuthenticated() {
		AdminPiiAccessPolicy p = policy();
		loginAsAdmin();

		assertTrue(p.canViewOriginal(PiiType.NAME, PiiGrade.STAFF));
	}

	@Test
	@DisplayName("미인증·익명은 등급과 무관하게 전부 마스킹(기본 차단 유지)")
	void anonymousMaskedRegardlessOfGrade() {
		AdminPiiAccessPolicy p = policy();

		SecurityContextHolder.clearContext();
		assertFalse(p.canViewOriginal(PiiType.NAME, PiiGrade.STAFF));
		assertFalse(p.canReveal(PiiType.NAME, PiiGrade.CUSTOMER));

		SecurityContextHolder.getContext().setAuthentication(
				new AnonymousAuthenticationToken("k", "anonymousUser",
						List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
		assertFalse(p.canViewOriginal(PiiType.NAME, PiiGrade.STAFF));
		assertFalse(p.canReveal(PiiType.NAME, PiiGrade.CUSTOMER));
	}

	@Test
	@DisplayName("표시는 가려도 관문은 열린다 — 축이 하나면 관문이 성립하지 않는다")
	void revealIsASeparateAxis() {
		AdminPiiAccessPolicy p = policy();
		loginAsAdmin();

		assertFalse(p.canViewOriginal(PiiType.MOBILE, PiiGrade.CUSTOMER), "화면 표시는 마스킹");
		assertTrue(p.canReveal(PiiType.MOBILE, PiiGrade.CUSTOMER), "사유를 남기는 열람은 가능");
	}
}
