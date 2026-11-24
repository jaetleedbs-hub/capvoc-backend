package ops.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 첨부 확장자 검사를 <b>점이 있는 파일명에만</b> 적용하는 형태를 막는다.
 *
 * <p><b>왜 grep 인가.</b> 이 결함은 논리 오류가 아니라 <b>복사</b>로 퍼졌다 —
 * 2026-09-04 조사에서 같은 형태가 <b>세 곳</b>에서 나왔다
 * ({@code FileAttachmentService} 두 곳 · {@code CompanyBaseInfoService} 한 곳).
 * 한 곳을 고쳐도 다음 사람이 옆의 코드를 보고 같은 형태를 또 만든다.
 * 그래서 <b>형태 자체</b>를 막는다.
 *
 * <p><b>무엇이 문제인가.</b> 검사 전체가 {@code if (fileName.contains("."))} 안쪽에 있으면
 * <b>확장자 없는 파일이 검증을 통째로 건너뛰고</b> 저장된다. 허용목록 방식에서 확장자가 없는
 * 파일은 애초에 목록에 있을 수 없으므로 <b>통과가 아니라 거부</b>가 맞다.
 * 통과시키면 「검사가 있다」는 외형만 남고 실제로는 우회로가 열려 있다 — 가장 나쁜 조합이다.
 */
class FileExtensionGateTest {

    private static final Path SRC = Paths.get("src/main/java");

    /** 확장자 검사를 「점이 있을 때만」으로 감싸는 형태. */
    private static final Pattern GATED_BY_DOT = Pattern.compile(
            "if\\s*\\([^)]*!=\\s*null\\s*&&\\s*[A-Za-z_][A-Za-z0-9_]*\\.contains\\s*\\(\\s*\"\\.\"\\s*\\)\\s*\\)");

    @Test
    @DisplayName("확장자 검사를 «점이 있을 때만» 으로 감싸지 않는다")
    void extensionCheckIsNotGatedByHavingADot() throws IOException {
        assertTrue(Files.isDirectory(SRC),
                "소스 폴더를 찾지 못했다. 시험이 도는 위치: " + Paths.get("").toAbsolutePath());

        List<String> hits = new ArrayList<>();
        try (Stream<Path> files = Files.walk(SRC)) {
            for (Path p : files.filter(f -> f.toString().endsWith(".java")).toList()) {
                String text = Files.readString(p, StandardCharsets.UTF_8);
                Matcher m = GATED_BY_DOT.matcher(text);
                while (m.find()) {
                    // 확장자 검사와 무관한 문자열 처리까지 잡지 않는다 —
                    // 바로 뒤에 lastIndexOf(".") 로 확장자를 꺼내는 것만 대상으로 본다.
                    String after = text.substring(m.end(), Math.min(text.length(), m.end() + 400));
                    if (after.contains("lastIndexOf(\".\")")) {
                        hits.add(SRC.relativize(p) + " : " + lineOf(text, m.start()));
                    }
                }
            }
        }

        assertTrue(hits.isEmpty(),
                "확장자 검사가 «점이 있을 때만» 도는 자리가 있다 — 점 없는 파일명은 검증 없이 통과한다.\n"
                        + "  " + String.join("\n  ", hits) + "\n"
                        + "조건을 뒤집어 «확장자가 없으면 거부» 로 바꾼다. 허용목록에 없는 것은 통과시키지 않는다.");
    }

    // ── 음성 대조 ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("음성 대조: 종전의 결함 형태를 실제로 잡아낸다")
    void detectsTheOldShape() {
        String defective = """
                String fileName = file.getOriginalFilename();
                if (fileName != null && fileName.contains(".")) {
                    String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                }
                """;
        assertTrue(GATED_BY_DOT.matcher(defective).find(), "결함 형태를 못 잡으면 이 시험은 무의미하다");
    }

    @Test
    @DisplayName("음성 대조: 고친 형태는 잡지 않는다")
    void acceptsTheFixedShape() {
        String fixed = """
                String fileName = file.getOriginalFilename();
                if (fileName == null || !fileName.contains(".")) {
                    throw new InvalidationException("확장자가 없는 파일은 업로드할 수 없습니다");
                }
                String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                """;
        assertFalse(GATED_BY_DOT.matcher(fixed).find(), "고친 형태를 잡으면 거짓 양성이다");
    }

    @Test
    @DisplayName("음성 대조: 확장자와 무관한 contains(\".\") 는 잡지 않는다")
    void ignoresUnrelatedDotChecks() throws IOException {
        // lastIndexOf(".") 가 뒤따르지 않으면 대상이 아니다 — 그 판정을 여기서 고정한다
        String unrelated = "if (path != null && path.contains(\".\")) { log.debug(path); }";
        Matcher m = GATED_BY_DOT.matcher(unrelated);
        assertTrue(m.find(), "패턴 자체는 문법상 일치한다");
        String after = unrelated.substring(m.end());
        assertFalse(after.contains("lastIndexOf(\".\")"), "그러나 확장자 추출이 없으므로 보고 대상이 아니다");
    }

    private static int lineOf(String text, int offset) {
        int line = 1;
        for (int i = 0; i < offset && i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }
}
