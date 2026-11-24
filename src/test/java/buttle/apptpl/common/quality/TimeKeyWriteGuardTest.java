package buttle.apptpl.common.quality;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import buttle.apptpl.common.util.IdGeneratorUtils;

/**
 * <b>시각을 유일성 근거로 되돌리지 못하게</b> 하는 감시 시험 (admin 쪽).
 *
 * <p>설계: {@code docs/design/design-pk-time-collision.md} · 마이그레이션 {@code V85}
 *
 * <p>desk 에도 같은 이름의 시험이 있다. 규칙은 하나인데 파일이 둘인 이유는 <b>저장소가 다르기
 * 때문</b>이다 — 한쪽 시험은 다른 쪽 소스를 볼 수 없다. 실제로 이 규칙을 처음 desk 에만 두었을
 * 때, admin 의 위반({@code IdGeneratorUtils})을 <b>보지 못한 채 통과</b>했다.
 */
class TimeKeyWriteGuardTest {

    private static final Path JAVA_DIR = Paths.get("src/main/java");

    // ─────────────────────────────────────────────────────────── A2 근태현황

    @Test
    @DisplayName("★ 근태현황 등록은 원자적이다 — 「세고 나서 없으면 넣는다」로 돌아가지 않았는가")
    void workStateInsertIsAtomic() throws IOException {
        Path mapper = Paths.get(
                "src/main/java/buttle/apptpl/api/common/components/mapper/CommonUserAuthMapper.xml");
        assertTrue(Files.exists(mapper), "매퍼가 없다(경로가 바뀌었나): " + mapper);

        String xml = Files.readString(mapper, StandardCharsets.UTF_8);
        Matcher m = Pattern.compile("<insert\\b[^>]*\\bid=\"insertWorkList\"[^>]*>(.*?)</insert>",
                Pattern.DOTALL).matcher(xml);
        assertTrue(m.find(), "insertWorkList 문장을 찾지 못했다 — 이 시험이 고장났다");

        String body = m.group(1);
        assertTrue(Pattern.compile("on\\s+conflict\\s*\\(\\s*crtr_ymd\\s*,\\s*user_id\\s*\\)",
                        Pattern.CASE_INSENSITIVE).matcher(body).find(),
                "insertWorkList 에 ON CONFLICT (crtr_ymd, user_id) 가 없다.\n"
                        + "같은 사용자의 로그인 요청이 겹치면 둘 다 넣어 PK 충돌로 500 이 난다.");

        // 짝이던 「등록 여부 조회」가 되살아나면 검사-후-삽입도 함께 돌아온다.
        assertTrue(!xml.contains("selectFirstWorkList"),
                "selectFirstWorkList 가 돌아왔다. 검사와 삽입 사이의 시간은 검사를 정교하게 해도 사라지지 않는다.");

        Path service = Paths.get(
                "src/main/java/buttle/apptpl/api/common/components/service/CommonUserAuthService.java");
        String java = Files.readString(service, StandardCharsets.UTF_8);
        assertTrue(!java.contains("selectFirstWorkList"),
                "서비스가 다시 세고 나서 넣는다: " + service);
    }

    // ─────────────────────────────────────────────────────────── E 식별자 생성

    @Test
    @DisplayName("★ 식별자를 시각으로 만들지 않는다 — 같은 초 2건이 같은 ID 였다")
    void generatedIdsAreUniqueWithinTheSameSecond() {
        // 옛 구현은 prefix + yyyyMMddHHmmss 였다. 아래 두 호출은 <같은 초 안에서> 끝나므로
        // 그 구현이었다면 같은 값이 나온다. 이 시험은 형태가 아니라 <성질>을 본다 —
        // 정규식으로 옛 코드 모양을 찾는 시험은 다른 모양으로 다시 쓰면 통과해 버린다
        // (desk 쪽에서 실제로 그렇게 새는 것을 확인하고 이 방식으로 바꿨다).
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 500; i++) {
            ids.add(IdGeneratorUtils.generateCampaignId());
        }
        assertEquals(500, ids.size(),
                "같은 초에 만든 식별자가 겹친다. 시각으로 ID 를 만들면 이렇게 된다.");

        assertTrue(IdGeneratorUtils.generateIdWithPrefix("CMP").startsWith("CMP"),
                "접두어 계약이 깨졌다");
    }

    @Test
    @DisplayName("시각 포맷을 식별자에 이어 붙이는 코드가 남아 있지 않은가 — 보조 그물")
    void noClockConcatenatedIdentifiers() throws IOException {
        // 위 시험이 본체다. 이것은 <아직 호출되지 않는> 새 코드를 잡기 위한 보조다.
        Pattern clockConcat = Pattern.compile(
                "\\+\\s*timestamp\\b|\\bprefix\\s*\\+\\s*[A-Za-z0-9_.]*[Ff]ormat");

        List<String> offenders = new ArrayList<>();
        try (Stream<Path> files = Files.walk(JAVA_DIR)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String java = stripJavaComments(Files.readString(file, StandardCharsets.UTF_8));
                if (!java.contains("yyyyMMddHHmmss")) {
                    continue;
                }
                if (clockConcat.matcher(java).find()) {
                    offenders.add("  " + JAVA_DIR.relativize(file));
                }
            }
        }
        assertTrue(offenders.isEmpty(),
                "시각을 이어 붙여 식별자를 만드는 곳이 있다. 같은 초에 두 건을 만들면 같은 ID 다:\n"
                        + String.join("\n", offenders));
    }

    /** 자바 주석만 걷는다 — SQL 의 {@code --} 규칙을 자바에 쓰면 멀쩡한 코드가 지워진다. */
    private static String stripJavaComments(String src) {
        return src.replaceAll("(?s)/\\*.*?\\*/", " ")
                .replaceAll("(?m)//[^\\n]*", " ");
    }
}
