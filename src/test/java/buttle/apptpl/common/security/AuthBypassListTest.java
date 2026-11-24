package buttle.apptpl.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * [§10.89] 인증·인가 <예외 목록>이 조용히 늘어나지 않게 고정한다.
 *
 * <p><b>왜 소스를 읽어 검사하나.</b> 두 목록은 각 클래스의 메서드 안에 있는 지역 {@code List.of(...)}
 * 라 밖에서 참조할 수 없다. 리팩터링해 꺼내는 편이 낫지만 그건 별 작업이고, 그때까지도
 * <b>목록이 늘어나는 것은 막아야 한다.</b>
 *
 * <p><b>무엇이 있었나.</b> {@code /api/external/} 이 <b>두 목록 모두</b>에 있었다. 즉 그 경로는
 * JWT 검사도 인가 검사도 받지 않았다. 정작 그 경로의 컨트롤러는 <b>하나도 없었는데</b>,
 * {@code DEVELOPMENT_GUIDE} 가 바로 그 경로에 컨트롤러를 만들라고 예시하고 있었다 —
 * 가이드를 따르는 순간 인증도 인가도 없는 API 가 생기고, 만든 사람은 알 방법이 없다.
 *
 * <p>dev 실측(2026-08-16)으로 확인했다: 다른 경로는 인가 관리자에 닿아 {@code [API-AUTHZ][AUDIT]}
 * 로그가 남는데 {@code /api/external/*} 만 <b>로그가 없다</b>(= 검사 자체를 하지 않는다).
 */
class AuthBypassListTest {

    private static final Path SRC = Path.of("src/main/java/buttle/apptpl/common");
    private static final Path JWT_FILTER = SRC.resolve("jwt/JwtAuthFilter.java");
    private static final Path AUTHZ_MANAGER = SRC.resolve("security/FwAuthorizationManager.java");

    /** 주석을 걷어낸 뒤, {@code excludedUrls} 목록의 문자열 리터럴만 뽑는다. */
    private List<String> excludedUrls(Path file) throws IOException {
        String src = Files.readString(file, StandardCharsets.UTF_8);

        int start = src.indexOf("excludedUrls");
        assertThat(start).as("%s 에 excludedUrls 목록이 있어야 한다", file).isGreaterThan(-1);
        int open = src.indexOf("List.of(", start);
        int close = src.indexOf(");", open);
        assertThat(open).isGreaterThan(-1);
        assertThat(close).isGreaterThan(open);

        String block = src.substring(open, close)
                // 주석 안의 예시 경로를 목록으로 오인하면 안 된다(이 파일의 설명 주석에도 경로가 있다).
                .replaceAll("(?s)/\\*.*?\\*/", "")
                .replaceAll("(?m)//.*$", "");

        Matcher m = Pattern.compile("\"([^\"]+)\"").matcher(block);
        return m.results().map((r) -> r.group(1)).toList();
    }

    @Test
    @DisplayName("JWT 검사 예외 목록에 /api/external/ 이 없다")
    void jwtFilterDoesNotBypassExternal() throws IOException {
        assertThat(excludedUrls(JWT_FILTER))
                .as("이 경로는 컨트롤러가 0건인데도 JWT 를 건너뛰고 있었다")
                .doesNotContain("/api/external/", "/api/external");
    }

    @Test
    @DisplayName("인가 검사 예외 목록에 /api/external/ 이 없다")
    void authorizationDoesNotBypassExternal() throws IOException {
        assertThat(excludedUrls(AUTHZ_MANAGER))
                .as("JwtAuthFilter 의 같은 항목과 한 벌이다 — 둘 다 있어야 무인증·무인가가 된다")
                .doesNotContain("/api/external/", "/api/external");
    }

    @Test
    @DisplayName("두 예외 목록이 예상 밖으로 늘어나지 않았다 — 늘릴 때는 이 테스트를 함께 고쳐야 한다")
    void bypassListsDoNotGrowSilently() throws IOException {
        // 목록에 한 줄 넣는 것은 <인증을 통째로 면제>하는 일인데, 지금은 코드 리뷰 말고
        // 그것을 알아챌 장치가 없다. 개수를 고정해 변경이 눈에 띄게 한다.
        assertThat(excludedUrls(JWT_FILTER))
                .as("JWT 예외 목록이 바뀌었다. 정말 인증 면제가 필요한지 확인하고 이 기대값을 고칠 것")
                .containsExactly(
                        "/login", "/error", "/jsonView", "/redirect", "/api/auth/",
                        "/api/common/user-auth", "/api/com0020/u01",
                        "/-/metrics", "/api-docs", "/v3/api-docs",
                        "/swagger-ui/", "/swagger-resources/", "/favicon.ico");
        assertThat(excludedUrls(AUTHZ_MANAGER))
                .as("인가 예외 목록이 바뀌었다. 같은 확인이 필요하다")
                .containsExactly(
                        "/login", "/error", "/jsonView", "/redirect",
                        "/api/auth/", "/api/common/polling/",
                        "/api/common/user-auth", "/api/com0020/u01",
                        "/api-docs", "/swagger-ui/", "/favicon.ico",
                        // [A3-1] 헬스 프로브. 인증 면제가 아니라 <화면 인가> 면제다 —
                        //  JwtAuthFilter 쪽에는 넣지 않는다(그쪽은 인증을 통째로 건너뛴다).
                        "/actuator/health");
    }
}
