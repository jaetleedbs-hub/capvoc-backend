package buttle.apptpl.common.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * <b>비밀값을 로그로 찍지 않는다</b> — 소스 전수 감시.
 *
 * <h3>왜 이 시험이 있는가 — 실제로 있었다 (2026-08-31 발견)</h3>
 * {@code ChannelSendAppService.encryptIfNeeded} 가 <b>AES 키 자체</b>를
 * {@code log.info("config.getAesKey():{}", envConfig.getAesKey())} 로 남기고 있었다.
 * 바로 위 줄에서는 복호한 전화·이메일도 찍었다.
 *
 * <p>그 키는 제품의 <b>모든 {@code _enc} 컬럼</b>을 지키는 값이다. 고객 이름·전화·이메일·주소,
 * 회원사 담당자 연락처, 개인정보 접근이력의 암호문까지 전부 같은 키다.
 * <b>로그 한 줄이 그 전부를 무력화한다.</b> 로그는 파일로 남고, 회전되고, 반출되기도 한다.
 *
 * <p>사람이 리뷰로 잡기를 기대할 수 없다 — 저 줄은 디버깅 중에 자연스럽게 추가되고,
 * 추가한 사람에게는 잠깐이었다. 그래서 <b>빌드가 막는다.</b>
 */
class NoSecretLoggingTest {

    private static final Path SRC = Paths.get("src/main/java");

    /**
     * 로그 호출 안에 <b>비밀값을 돌려주는 접근자</b>가 인자로 들어가는 것을 잡는다.
     *
     * <p>문자열 안의 이름(예: {@code log.info("aesKey 길이: {}", key.length())})은 대상이 아니다 —
     * 길이나 존재 여부는 값이 아니다. 값을 <b>그대로</b> 넘기는 것만 막는다.
     */
    private static final Pattern SECRET_IN_LOG = Pattern.compile(
            "log\\.(?:trace|debug|info|warn|error)\\s*\\([^;]*?"
                    + "(getAesKey\\s*\\(\\s*\\)|getSecret\\s*\\(\\s*\\)|getPswd\\s*\\(\\s*\\)|getPassword\\s*\\(\\s*\\))",
            Pattern.DOTALL);

    @Test
    @DisplayName("★ AES 키·시크릿·비밀번호를 로그 인자로 넘기지 않는다")
    void noSecretIsPassedToLogger() throws IOException {
        List<String> violations = new ArrayList<>();
        int scanned = 0;

        try (Stream<Path> files = Files.walk(SRC)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                scanned++;
                String src = Files.readString(file, StandardCharsets.UTF_8);
                Matcher m = SECRET_IN_LOG.matcher(src);
                while (m.find()) {
                    int line = (int) src.substring(0, m.start()).chars().filter(c -> c == '\n').count() + 1;
                    violations.add("  %s:%d — 로그 인자에 %s".formatted(
                            SRC.relativize(file), line, m.group(1)));
                }
            }
        }

        // 파일을 하나도 안 훑었으면 <시험이 고장난 것>이다. 0건을 통과로 읽지 않는다.
        assertTrue(scanned > 100, "훑은 소스가 %d개뿐이다. 경로가 어긋났을 수 있다".formatted(scanned));

        assertTrue(violations.isEmpty(),
                "비밀값이 로그로 나간다. 로그는 파일로 남고 반출된다 — 값 대신 <결과>를 남길 것:\n"
                        + String.join("\n", violations));
    }
}
