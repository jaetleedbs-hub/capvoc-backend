package buttle.apptpl.common.config.caffeine;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import buttle.apptpl.common.framework.common.vo.SecUser;

/**
 * [§10.99] 캐시에서 복원한 인증의 <b>권한</b> 회귀 테스트.
 *
 * <p>종전에는 빈 목록이었다. 토큰을 파싱하는 경로는 권한을 채우는데 <b>캐시가 맞은 경로만</b>
 * 비어서, <b>첫 요청에는 권한이 있고 그 다음부터 없는</b> 상태였다 — 재현이 들쭉날쭉해
 * "가끔 권한이 없다"로 넘어가기 쉬운 종류다. 그래서 값으로 고정한다.
 */
class CaffeineJwtAuthorityTest {

    @SuppressWarnings("unchecked")
    private List<String> authoritiesOf(SecUser user) throws Exception {
        CaffeineJwtRepository repo = new CaffeineJwtRepository(null);
        Method m = CaffeineJwtRepository.class.getDeclaredMethod("getAuthentication", SecUser.class);
        m.setAccessible(true);
        Authentication auth = (Authentication) m.invoke(repo, user);
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().toList();
    }

    @Test
    @DisplayName("권한 목록이 있으면 그대로 복원한다")
    void restoresFromList() throws Exception {
        SecUser user = SecUser.builder().userId("u1").authrtCdList(List.of("PROJECT", "SYSADMIN")).build();
        assertThat(authoritiesOf(user)).containsExactly("PROJECT", "SYSADMIN");
    }

    @Test
    @DisplayName("목록이 없으면 단일 권한코드로 복원한다 — 캐시에 담긴 형태가 두 가지다")
    void fallsBackToSingleCode() throws Exception {
        SecUser user = SecUser.builder().userId("u1").authrtCd("PROJECT").build();
        assertThat(authoritiesOf(user)).containsExactly("PROJECT");
    }

    @Test
    @DisplayName("둘 다 없으면 비워 둔다 — 없는 권한을 지어내지 않는다")
    void inventsNothing() throws Exception {
        assertThat(authoritiesOf(SecUser.builder().userId("u1").build())).isEmpty();
    }

    @Test
    @DisplayName("빈 문자열은 권한으로 세지 않는다")
    void ignoresBlank() throws Exception {
        SecUser user = SecUser.builder().userId("u1").authrtCd("   ").build();
        assertThat(authoritiesOf(user)).isEmpty();
    }

    @Test
    @DisplayName("주체는 SecUser 그대로다 — 인가가 이 타입을 본다(§10.98)")
    void principalStaysSecUser() throws Exception {
        SecUser user = SecUser.builder().userId("u1").authrtCd("PROJECT").build();
        CaffeineJwtRepository repo = new CaffeineJwtRepository(null);
        Method m = CaffeineJwtRepository.class.getDeclaredMethod("getAuthentication", SecUser.class);
        m.setAccessible(true);
        Authentication auth = (Authentication) m.invoke(repo, user);
        assertThat(auth.getPrincipal()).isInstanceOf(SecUser.class);
    }
}
