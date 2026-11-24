package buttle.apptpl.common.security;

/**
 * 관리자 API 권한 검사(프로그램-권한 매핑) 정책.
 *
 * <h3>왜 boolean 이 아니라 3단계인가</h3>
 * 종전에는 {@code svc-env.api-authentication} 이 boolean 이었고, 모든 환경에서 {@code false} 였다.
 * {@code false} 면 {@code FwAuthorizationManager} 가 즉시 통과를 반환하므로 <b>로그인만 하면
 * 권한코드와 무관하게 모든 관리자 API 를 호출할 수 있었다</b>.
 *
 * <p>그렇다고 곧바로 {@code true} 로 올리면, 프로그램-권한 매핑에 빠진 엔드포인트가 하나라도 있을 때
 * 그 화면이 즉시 403 이 된다. 무엇이 빠졌는지 <b>미리 알 방법이 없다는 것</b>이 지금까지 켜지 못한
 * 진짜 이유다.
 *
 * <p>그래서 검사는 수행하되 차단은 하지 않고 기록만 남기는 중간 단계를 둔다. desk 의
 * {@code unmapped-url-policy}, search 의 {@code internal-api.auth-policy} 와 같은 전환 방식이다.
 *
 * <p>전환 순서: OFF → AUDIT(누락 매핑 수집) → ENFORCE(차단).
 * 로컬·dev 는 AUDIT, demo 는 ENFORCE 로 운영한다.
 */
public enum ApiAuthPolicy {

    /** 검사하지 않는다. 종전 {@code api-authentication: false} 와 같은 동작. */
    OFF,

    /**
     * 검사하되 <b>거부 대상도 통과</b>시키고 경고를 남긴다.
     * 로그에 쌓인 URI 를 프로그램-권한 매핑에 등록한 뒤 ENFORCE 로 올린다.
     */
    AUDIT,

    /** 권한 매핑에 없거나 미인증이면 차단한다. 최종 목표 상태. */
    ENFORCE;

    public boolean isEnforcing() {
        return this == ENFORCE;
    }

    /**
     * 구 설정({@code api-authentication}) 과의 호환 해석.
     *
     * @param policy       신규 {@code svc-env.api-auth-policy} (없으면 null)
     * @param legacyEnabled 구 {@code svc-env.api-authentication} (없으면 null)
     */
    public static ApiAuthPolicy resolve(ApiAuthPolicy policy, Boolean legacyEnabled) {
        if (policy != null) {
            return policy;
        }
        return Boolean.TRUE.equals(legacyEnabled) ? ENFORCE : OFF;
    }
}
