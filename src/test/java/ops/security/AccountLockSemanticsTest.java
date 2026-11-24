package ops.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 계정 잠금 판정의 <b>부호</b>와 <b>판정 자리</b>를 고정한다.
 *
 * <p><b>왜 이 시험이 있나.</b> 두 이름이 정반대 계약을 쓴다.
 *
 * <table>
 *   <tr><th>이름</th><th>true 의 뜻</th></tr>
 *   <tr><td>{@code LoginAttemptUtils.isAccountLocked}</td><td><b>잠김</b></td></tr>
 *   <tr><td>{@code UserDetails.isAccountNonLocked}(Spring 계약)</td><td><b>안 잠김</b></td></tr>
 * </table>
 *
 * <p>종전에는 앞의 것이 {@code isAccountNonLocked} 라는 이름으로 <b>잠겼을 때 true</b> 를
 * 돌려주고 있었다. 호출부 둘이 {@code if (…) throw} 라 우연히 맞게 돌았을 뿐이고,
 * 2026-09-05 에 이 코드를 읽은 두 사람이 모두 「Spring 쪽에 그대로 이으면 된다」고 판단했다.
 * <b>그대로 이었으면 잠금이 뒤집혀 잠긴 계정만 통과했다.</b>
 *
 * <p>그래서 고친 것은 동작이 아니라 <b>이름</b>이고, 이 시험은 그 이름이 되돌아오지 못하게 한다.
 * 동작을 재는 시험이 아니라 <b>다음 사람이 같은 오독을 하지 못하게 하는</b> 시험이다.
 */
class AccountLockSemanticsTest {

    private static final Path UTILS =
            Paths.get("src/main/java/buttle/apptpl/common/security/bruteforce/LoginAttemptUtils.java");
    private static final Path CHECKER =
            Paths.get("src/main/java/buttle/apptpl/common/security/CustomUserDetailsChecker.java");
    private static final Path SEC_CONFIG =
            Paths.get("src/main/java/buttle/apptpl/common/security/SecurityConfig.java");
    private static final Path DETAILS =
            Paths.get("src/main/java/buttle/apptpl/common/framework/common/vo/CustomUserDetails.java");

    private static String read(Path p) throws IOException {
        assertTrue(Files.exists(p), "파일을 찾지 못했다: " + p.toAbsolutePath());
        return Files.readString(p, StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("잠금 판정 메서드 이름이 «잠기면 true» 를 말한다")
    void predicateIsNamedForWhatItReturns() throws IOException {
        String src = read(UTILS);
        assertTrue(src.contains("public boolean isAccountLocked(CmptUserEntity"),
                "LoginAttemptUtils.isAccountLocked 가 없다 — 이름이 되돌아갔는가?");
        assertFalse(src.contains("public boolean isAccountNonLocked(CmptUserEntity"),
                "옛 이름 isAccountNonLocked 가 되살아났다. 그 이름은 <잠기면 true> 를 돌려주므로 "
                        + "Spring 의 UserDetails.isAccountNonLocked(=안 잠기면 true) 와 정반대다.");
    }

    @Test
    @DisplayName("판정식이 여전히 «임계 도달 = 잠김» 이다")
    void predicateStillMeansThresholdReached() throws IOException {
        String src = read(UTILS);
        int at = src.indexOf("public boolean isAccountLocked(CmptUserEntity");
        assertTrue(at > 0, "메서드를 찾지 못했다");
        String body = src.substring(at, Math.min(src.length(), at + 600));
        assertTrue(body.contains("userTryCnt >= maxAttemptCnt"),
                "판정식이 바뀌었다. 부호가 뒤집히면 잠긴 계정만 통과하게 된다.");
    }

    @Test
    @DisplayName("잠금을 실제로 막는 곳은 체커이고, 그 체커가 인증에 물려 있다")
    void checkerIsTheRealGateAndIsWired() throws IOException {
        String checker = read(CHECKER);
        assertTrue(checker.contains("isAccountLocked(userEntity)"),
                "체커가 잠금 검사를 하지 않는다");
        assertTrue(checker.contains("CustomAuthenticationException"),
                "체커가 거부하지 않고 통과시킨다");

        String cfg = read(SEC_CONFIG);
        assertTrue(cfg.contains("setPostAuthenticationChecks(userDetailsChecker())"),
                "체커가 AuthenticationProvider 에 물려 있지 않다 — 물리지 않으면 잠금이 아무 데서도 안 걸린다");
    }

    @Test
    @DisplayName("UserDetails 플래그가 true 인 이유가 코드에 적혀 있다")
    void hardcodedFlagsCarryTheirReason() throws IOException {
        String src = read(DETAILS);
        assertTrue(src.contains("CustomUserDetailsChecker"),
                "세 플래그가 왜 true 인지 적혀 있지 않다. 설명이 없으면 «잠금이 꺼져 있다» 로 읽히고, "
                        + "실제로 2026-09-05 에 두 번 그렇게 읽혔다.");
    }
}
