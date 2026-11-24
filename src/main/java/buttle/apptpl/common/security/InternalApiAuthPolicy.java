package buttle.apptpl.common.security;

/**
 * 내부 서비스 간(S2S) API 인증 정책.
 *
 * <p>{@code CompanyApiAuthFilter}는 {@code X-Member-Id} 등 <b>검증되지 않은 요청 헤더만으로</b>
 * 인증 주체를 만들고 {@code ROLE_USER}·{@code ROLE_ADMIN}까지 부여했다. admin-backend 포트에
 * 도달할 수 있는 누구나 헤더 하나로 관리자가 될 수 있었다.
 *
 * <p>desk-backend 가 같은 결함을 W0-4 로 고칠 때 쓴 구조를 그대로 가져온다 —
 * 사전 공유 키({@code X-Api-Key})를 검증하고, 전환 기간에는 기록만 한다.
 *
 * <p>전환 순서: AUDIT(기록만) → ENFORCE(차단).
 * 로컬·dev 는 AUDIT, demo 는 ENFORCE 로 운영한다.
 *
 * @see buttle.apptpl.common.adapter.filter.CompanyApiAuthFilter
 */
public enum InternalApiAuthPolicy {

    /**
     * 키를 검증하되 <b>불일치해도 통과</b>시키고 경고만 남긴다.
     * 호출 측(desk {@code AdminApiAdapter})의 키 설정이 반영됐는지 확인하는 전환 단계다.
     */
    AUDIT,

    /** 키가 없거나 틀리면 신원 헤더를 신뢰하지 않고 401 로 차단한다. 최종 목표 상태. */
    ENFORCE;

    public boolean isEnforcing() {
        return this == ENFORCE;
    }
}
