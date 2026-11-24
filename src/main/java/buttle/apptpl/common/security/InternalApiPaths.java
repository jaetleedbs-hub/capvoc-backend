package buttle.apptpl.common.security;

import java.util.List;

/**
 * 서버 간(S2S) 전용 API 경로 정책 — <b>사전 공유 키만으로 열어 줄 경로가 어디까지인가</b>의 단일 출처.
 *
 * <h3>왜 필요한가 — 키가 만능키였다</h3>
 * {@code CompanyApiAuthFilter} 는 §10.88 에서 <b>신원 헤더 없이 키만 제시한 호출</b>도 인증하도록
 * 고쳤다. 그 자체는 옳았으나 <b>경로 제한이 없었다</b> — 키만 맞으면 어떤 admin API 든
 * {@code ATTR_INTERNAL_S2S} 표식을 받고, 그 표식은 {@code FwAuthorizationManager} 에서
 * <b>인가 검사를 통째로 우회</b>시킨다.
 *
 * <p>실측(2026-08-20, dev)에서 그대로 재현됐다. 키 하나로 무관한 화면 API 가 열렸다.
 *
 * <pre>
 *   GET /api/log/batch/jobs                    키 없이 302  →  키만 200
 *   GET /api/cmpy/list/members/apt001/contact              →  키만 200
 *   GET /api/common/menu/list                  키 없이 302  →  키만 500(컨트롤러까지 도달)
 * </pre>
 *
 * A3-1 로 <b>사람 경로</b>는 ENFORCE 로 잠갔는데, S2S 경로만 경로 제한 없이 열려 있었다.
 * {@code CompanyApiAuthFilter} 주석도 스스로 *"필요해지면 그때 경로별로 부여해야 한다"* 고
 * 적어 두고 있었다 — 그 시점이 지금이다.
 *
 * <h3>목록은 <b>추측이 아니라 실측</b>으로 정했다</h3>
 * dev 컨테이너 로그에서 {@code S2S 인증 성공(신원 헤더 없음)} 이 실제로 찍힌 경로를 셌다.
 * 기동 이래 26회 호출된 {@code /api/cmpy/type-cd/cmpy-type-code} 하나뿐이었고,
 * 나머지 셋은 이 결함을 확인하려고 내가 던진 탐침이었다.
 *
 * <p>호출자는 desk 의 {@code CmptCdSyncService}(회원사 유형코드 동기화)다 —
 * 사람이 없는 호출자라 신원 헤더가 없고, 그래서 키만 보낸다.
 *
 * <h3>신원 헤더가 있는 호출은 이 목록과 무관하다</h3>
 * {@code X-Member-Id} 를 함께 보내는 호출({@code /api/admin/polling/all} 등)은
 * {@code CompanyApiAuthFilter} 의 <b>다른 분기</b>를 탄다. 그쪽은 종전대로 동작하며
 * 이 목록의 영향을 받지 않는다.
 *
 * <h3>프리픽스보다 정확 경로를 쓴다</h3>
 * {@code /api/cmpy/} 로 묶으면 {@code /api/cmpy/list/members/...} 같은 <b>형제 경로가 함께 열린다</b>.
 * 그 경로는 회원사 임직원 연락처를 돌려주므로 키 하나로 열려 있어서는 안 된다.
 * desk 의 같은 이름 클래스가 {@code PROTECTED_EXACT} 를 둔 이유와 같다.
 */
public final class InternalApiPaths {

    /**
     * 키만으로(신원 헤더 없이) 열어 줄 <b>정확한</b> 경로.
     *
     * <p>여기에 없는 경로는 키를 맞게 제시해도 S2S 주체가 서지 않는다 —
     * 인가 우회 표식도 받지 못한다.
     *
     * <p><b>새 S2S 엔드포인트를 만들면 여기에 등록해야 한다.</b> 빠뜨리면 런타임 401/403 으로만
     * 드러나고, 자주 쓰이지 않는 경로면 아무도 모른 채 방치된다(desk 의 {@code pre-notify} 선례).
     */
    public static final List<String> PROTECTED_EXACT = List.of(
            // [실측 2026-08-20] desk CmptCdSyncService 가 회원사 유형코드를 동기화한다.
            //  기동 이래 26회. 사람이 없는 호출자라 신원 헤더가 없다.
            "/api/cmpy/type-cd/cmpy-type-code",
            // [A5-1 §2.1] 통계 수집 신호 수신. desk 등이 <이 시간버킷이 바뀌었다>를 알린다.
            //  사람이 아니라 프로세스가 부르므로 신원 헤더가 없다.
            "/api/internal/stats/signal");

    /**
     * 프리픽스 단위 보호 — 하위 경로가 자동 포함된다.
     *
     * <p><b>지금은 비어 있다.</b> 실측으로 확인된 키-only 호출자가 하나뿐이라 프리픽스를 열 이유가 없다.
     * 넓게 열면 그 아래 신규 API 가 <b>자동으로 키 하나에 노출</b>되므로, 필요해질 때
     * <b>그 경로가 실제로 그렇게 호출된다는 근거</b>와 함께 추가한다.
     */
    public static final List<String> PROTECTED_PREFIXES = List.of();

    /** 이 경로가 키만으로 S2S 인증을 받을 수 있는가. */
    public static boolean isKeyOnlyAllowed(String uri) {
        if (uri == null || uri.isEmpty()) {
            return false;
        }
        if (PROTECTED_EXACT.contains(uri)) {
            return true;
        }
        for (String prefix : PROTECTED_PREFIXES) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private InternalApiPaths() {
    }
}
