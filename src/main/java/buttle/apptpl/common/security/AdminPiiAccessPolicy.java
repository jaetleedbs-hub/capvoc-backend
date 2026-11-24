package buttle.apptpl.common.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.pii.PiiAccessPolicy;
import micoai.corebase.pii.PiiContext;
import micoai.corebase.pii.PiiGrade;
import micoai.corebase.pii.PiiType;

/**
 * admin 의 개인정보 원본 열람 권한 정책. corebase {@link PiiContext} 에 등록해 응답 직렬화 시
 * 마스킹/원본을 가른다.
 *
 * <h3>운영 콘솔은 <고객> 개인정보의 취급자가 아니다 (2026-08-13 결정)</h3>
 * admin 은 서비스 운영사가 회원사를 관리하는 콘솔이다. 그래서 <b>고객 등급({@code CUSTOMER})은
 * 인증 여부와 무관하게 항상 마스킹</b>하고, 임직원 등급({@code STAFF})만 인증 시 원본을 준다 —
 * 계정·조직·회원사 관리에서 임직원 이름을 가리면 업무가 성립하지 않기 때문이다.
 *
 * <h3>지금은 <가릴 대상이 0건>이다 — 그래도 두는 이유</h3>
 * 활성 메뉴 53개를 전수로 확인한 결과 admin 에는 <b>고객 데이터를 조회하는 화면이 없다</b>
 * (채널 발송 대상은 {@code cmm_mbr_inf} 임직원, 자기 스키마의 상담 테이블은 전부 0행·집계 전용).
 * 따라서 이 정책은 <b>지금 동작을 바꾸지 않는다.</b>
 *
 * <p>그럼에도 두는 것은 <b>안전망</b>이기 때문이다. 나중에 admin 이 고객 데이터를 다루게 되면
 * (통합 상담 이력 조회 등) 그때 {@code @Pii} 를 붙이는 순간 <b>자동으로 가려진다</b> —
 * 기본값이 "보인다"이면 그 시점에 아무도 알아채지 못한 채 노출된다. 기본 차단이 이 프로젝트의 규약이다.
 *
 * <p><b>열람 관문은 만들지 않았다</b>: 가려지는 대상이 없으므로 열 것도 없다. 필요해지는 시점은
 * admin 에 고객 데이터가 들어오는 때이고, 그때 corebase 의 {@code canReveal} 축(2.1.22)을 쓰면 된다.
 *
 * <p><b>전면 마스킹 스위치</b>: {@code corebase.pii.masking-enabled: true} 를 주면 위와 같은 기준으로
 * 고객 등급을 가린다(세 앱이 같은 키를 쓴다). 이 정책에서는 고객 등급이 이미 항상 마스킹이라 차이가 없다.
 */
@Slf4j
@Component
public class AdminPiiAccessPolicy implements PiiAccessPolicy {

	// [의도적으로 읽지 않는다] corebase.pii.masking-enabled 스위치는 admin 에 영향이 없다.
	//  고객 등급은 스위치와 무관하게 <항상> 마스킹이고, 임직원 등급은 스위치가 켜져도 원본이기 때문이다
	//  (스위치 자체가 고객 등급만 가리는 설계 — §10.52). 읽지도 않는 설정을 필드로 들고 있으면
	//  "정책이 설정돼 있다"고 오독된다(§10.62 에서 지운 svc-env.masking 과 같은 부류라 두지 않는다).

	@PostConstruct
	public void register() {
		PiiContext.setPolicy(this);
		log.info("[PII] AdminPiiAccessPolicy 등록 — 고객 등급은 항상 마스킹(운영 콘솔은 취급자가 아니다), 임직원 등급은 인증 시 원본");
	}

	@Override
	public boolean canViewOriginal(PiiType type) {
		return canViewOriginal(type, PiiGrade.CUSTOMER);
	}

	@Override
	public boolean canViewOriginal(PiiType type, PiiGrade grade) {
		// [안전망] 고객 등급은 <인증 여부·스위치와 무관하게> 항상 마스킹한다.
		// 운영 콘솔은 고객 개인정보의 취급자가 아니다. 지금은 고객 데이터를 다루는 화면이 없어
		// 실제로 가려지는 것이 없지만, 나중에 들어오면 부착하는 순간 자동으로 가려진다.
		if (grade != PiiGrade.STAFF) {
			return false;
		}
		// 임직원 등급은 계정·조직 관리에 필요하므로 인증된 관리자에게 원본을 준다.
		// [주의] SecurityUtils.getCurrentUser() 는 미인증 시에도 <익명 SecUser>를 반환하므로
		// null 검사만으로는 익명 사용자에게 원본이 노출된다. Authentication 을 직접 보고 익명을 배제한다.
		return authenticatedAdmin();
	}

	/**
	 * 열람 관문({@code PiiReveal}) 통과 권한 — <b>표시 권한과 다른 축</b>이다(corebase 2.1.22).
	 *
	 * <p>고객 등급을 화면에서 가리는 것과, 필요할 때 <b>사유를 남기고 여는 것</b>은 다른 질문이다.
	 * 두 판단을 같은 축에 두면 가려진 사람이 열지도 못해 관문이 성립하지 않는다.
	 *
	 * <p>여기서는 <b>인증된 관리자면 통과</b>시킨다. 통제는 "누가 볼 수 있는가"가 아니라
	 * <b>"열면 반드시 남는다"</b> 쪽에 둔다 — {@code PiiReveal} 은 감사 기록에 실패하면 원본을
	 * 반환하지 않는다. 취급자 지정 절차가 생기면 이 지점만 좁히면 된다.
	 */
	@Override
	public boolean canReveal(PiiType type, PiiGrade grade) {
		return authenticatedAdmin();
	}

	/** 인증된(익명 아닌) 실제 관리자 주체인가. */
	private boolean authenticatedAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return false; // 미인증/익명 → 마스킹·거부
		}
		return auth.getPrincipal() instanceof SecUser;
	}
}
