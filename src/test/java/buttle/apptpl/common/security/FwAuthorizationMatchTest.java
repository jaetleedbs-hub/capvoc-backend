package buttle.apptpl.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * [§10.98] 인가 매칭 회귀 테스트.
 *
 * <p>여기서 고정하는 것은 <b>패턴이 열어 주는 범위</b>다. 경로변수를 매칭하려고 패턴을 들이면
 * 반대 위험이 생긴다 — 너무 넓게 잡으면 등록하지 않은 경로까지 통과한다. 인가에서 그건
 * "검사를 안 하는 것"과 같다.
 *
 * <p>private 메서드를 리플렉션으로 부른다. 이 판정은 <b>공개 API 가 아니라</b> 인가 내부 규칙이고,
 * 테스트를 위해 가시성을 넓히면 다른 곳에서 부를 수 있게 된다.
 */
class FwAuthorizationMatchTest {

    private boolean matches(List<String> registered, String uri) throws Exception {
        FwAuthorizationManager m = new FwAuthorizationManager(null, null);
        Method method = FwAuthorizationManager.class.getDeclaredMethod("matches", List.class, String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(m, registered, uri);
    }

    @Test
    @DisplayName("고정 경로는 완전일치로만 통과한다 — 접두어가 같다고 열리면 안 된다")
    void exactOnlyForPlainPaths() throws Exception {
        List<String> reg = List.of("/api/cmpy/list/members");

        assertThat(matches(reg, "/api/cmpy/list/members")).isTrue();
        assertThat(matches(reg, "/api/cmpy/list/members/detail/apt001")).isFalse();
        assertThat(matches(reg, "/api/cmpy/list")).isFalse();
    }

    @Test
    @DisplayName("패턴 등록은 경로변수 한 칸을 덮는다")
    void patternCoversPathVariable() throws Exception {
        List<String> reg = List.of("/api/common/files/*");

        assertThat(matches(reg, "/api/common/files/abc-123")).isTrue();
        assertThat(matches(reg, "/api/common/files")).isFalse();
        // 한 칸짜리 패턴이 여러 칸을 삼키면 안 된다 — 그러면 하위 경로가 통째로 열린다.
        assertThat(matches(reg, "/api/common/files/abc/delete")).isFalse();
    }

    @Test
    @DisplayName("** 는 하위 전체를 덮는다 — 의도한 곳에만 쓴다")
    void doubleStarCoversSubtree() throws Exception {
        List<String> reg = List.of("/api/common/external/download/**");

        assertThat(matches(reg, "/api/common/external/download/f1")).isTrue();
        assertThat(matches(reg, "/api/common/external/download/f1/raw")).isTrue();
        assertThat(matches(reg, "/api/common/external/downloads")).isFalse();
    }

    @Test
    @DisplayName("등록이 비면 아무것도 통과하지 않는다 — 빈 목록이 개방이 되면 안 된다")
    void emptyRegistrationOpensNothing() throws Exception {
        assertThat(matches(List.of(), "/api/anything")).isFalse();
        assertThat(matches(null, "/api/anything")).isFalse();
    }

    @Test
    @DisplayName("요청 URI 가 null 이면 통과시키지 않는다")
    void nullUriDoesNotPass() throws Exception {
        assertThat(matches(List.of("/api/**"), null)).isFalse();
    }
}
