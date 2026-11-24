package ops.env;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code .env.example} 이 <채우지 않으면 동작하지 않는 변수>를 빠짐없이 담고 있는지 강제한다.
 *
 * <p><b>왜 시험인가.</b> 템플릿 갱신은 사람이 기억해야 하는 일이라 반드시 낡는다.
 * 새 환경변수를 읽기 시작한 사람과, 나중에 저장소를 받아 {@code .env} 를 채우는 사람이
 * 서로 다르기 때문이다. 빠뜨린 키는 <b>기동 실패가 아니라 조용한 오동작</b>으로 나타나므로
 * (빈 값으로 뜬다) 받은 사람은 무엇이 빠졌는지 알 방법이 없다. 그래서 시험이 지킨다.
 *
 * <p><b>왜 {@code .env} 가 아니라 {@code application.yml} 이 근거인가.</b>
 * {@code .env} 는 gitignore 대상이라 <새로 받은 사람의 작업트리와 CI 에는 없다.>
 * 없는 파일을 근거로 삼으면 시험은 정작 필요한 자리에서 조용히 통과한다.
 * 반면 yml 은 저장소에 있고, 앱이 실제로 무엇을 읽는지에 대한 정본이다.
 *
 * <p><b>판정 규칙</b> — 주석을 제거한 yml 본문에서 {@code ${VAR...}} 을 모으고,
 * 그 변수의 <b>모든</b> 등장이 「기본값 없음」({@code ${VAR}}) 또는 「빈 기본값」({@code ${VAR:}})
 * 이면 템플릿에 있어야 한다. 한 번이라도 쓸 수 있는 기본값이 있으면 제외한다 —
 * {@code application-local.yml} 이 로컬 기본값을 주는 키가 그 경우다.
 */
class EnvExampleCompletenessTest {

    /** 로컬 개발이 읽는 yml. 없는 파일은 건너뛴다(저장소마다 프로파일 구성이 다르다). */
    private static final List<String> YAML_PATHS = List.of(
            "src/main/resources/application.yml",
            "src/main/resources/application-local.yml");

    private static final String TEMPLATE_PATH = ".env.example";

    @Test
    @DisplayName(".env.example 은 채우지 않으면 동작하지 않는 변수를 모두 담아야 한다")
    void templateCoversEveryRequiredVariable() throws IOException {
        List<String> yamls = new ArrayList<>();
        for (String p : YAML_PATHS) {
            Path path = Paths.get(p);
            if (Files.exists(path)) {
                yamls.add(Files.readString(path, StandardCharsets.UTF_8));
            }
        }
        assertFalse(yamls.isEmpty(),
                "application*.yml 을 찾지 못했다. 시험이 도는 위치: " + Paths.get("").toAbsolutePath());

        Path template = Paths.get(TEMPLATE_PATH);
        assertTrue(Files.exists(template),
                TEMPLATE_PATH + " 이 없다. 새로 받은 사람이 무엇을 채워야 하는지 알 방법이 없다. "
                        + "찾은 위치: " + template.toAbsolutePath());

        Set<String> required = requiredNames(yamls);
        Set<String> declared = templateKeys(Files.readString(template, StandardCharsets.UTF_8));

        List<String> missing = new ArrayList<>();
        for (String name : required) {
            if (!declared.contains(name)) {
                missing.add(name);
            }
        }

        assertTrue(missing.isEmpty(),
                "application*.yml 은 이 변수들을 <기본값 없이> 읽는데 " + TEMPLATE_PATH + " 에 없다.\n"
                        + "  누락: " + missing + "\n"
                        + "각 키에 «없으면 무엇이 안 되는가»를 주석으로 달아 " + TEMPLATE_PATH + " 에 추가한다.\n"
                        + "값이 시크릿이면 빈 채로 두고, 어디서 받는지를 README 환경설정 절에 적는다.");
    }

    // ── 음성 대조 ────────────────────────────────────────────────────────────
    // 위 시험이 <통과했다>는 것만으로는 판정이 살아 있는지 알 수 없다.
    // 규칙이 실제로 잡아내는지, 그리고 잡지 말아야 할 것을 잡지 않는지를 여기서 증명한다.

    @Test
    @DisplayName("음성 대조: 빈 기본값이 템플릿에 없으면 잡아낸다")
    void detectsMissingKey() {
        Set<String> required = requiredNames(List.of("mail:\n  from: ${MAIL_FROM:}\n"));
        assertEquals(Set.of("MAIL_FROM"), required);
        assertFalse(templateKeys("DB_PASSWORD=\n").contains("MAIL_FROM"));
    }

    @Test
    @DisplayName("음성 대조: 기본값이 있으면 필수가 아니다 — 로컬 프로파일이 준 값 포함")
    void usableDefaultIsNotRequired() {
        assertEquals(Set.of(), requiredNames(List.of("a: ${WITH_DEFAULT:something}\n")));
        // application.yml 은 비워 두고 application-local.yml 이 로컬값을 주는 실제 형태
        assertEquals(Set.of(), requiredNames(List.of(
                "key: ${INTERNAL_API_KEY:}\n",
                "key: ${INTERNAL_API_KEY:local-api-key}\n")));
    }

    @Test
    @DisplayName("음성 대조: 주석 안의 변수는 앱이 읽지 않으므로 필수가 아니다")
    void commentedOutReferenceIsNotRequired() {
        // 설명 주석에 남아 있는 옛 변수 참조를 필수로 오인하지 않아야 한다
        assertEquals(Set.of(), requiredNames(List.of("#   종전에는 gw: ${OLD_GATEWAY_KEY:} 였다\n")));
        // 따옴표 안의 # 는 주석이 아니다
        assertEquals(Set.of("REAL"), requiredNames(List.of("a: \"# ${REAL:}\"\n")));
    }

    @Test
    @DisplayName("음성 대조: 중첩 기본값은 안쪽이 판정한다")
    void nestedDefaultIsResolved() {
        // ${A:${B:}} — 안쪽까지 따라가면 결국 빈 값이라 필수
        assertEquals(Set.of("OUTER"), requiredNames(List.of("a: ${OUTER:${INNER:}}\n")));
        // 안쪽이 실값을 주면 필수가 아니다
        assertEquals(Set.of(), requiredNames(List.of("a: ${OUTER:${INNER:fallback}}\n")));
    }

    @Test
    @DisplayName("음성 대조: 주석 처리된 템플릿 줄도 <선언됨>으로 읽는다")
    void commentedTemplateLineCounts() {
        // 목적은 «키가 보이는가»다. 주석으로 설명해 둔 키는 보인다
        assertTrue(templateKeys("# PORT=80\nDB_PASSWORD=\n").contains("PORT"));
        assertTrue(templateKeys("# PORT=80\nDB_PASSWORD=\n").contains("DB_PASSWORD"));
    }

    // ── 판정 본체 ────────────────────────────────────────────────────────────

    /** 모든 등장이 「쓸 수 있는 기본값 없음」인 변수 이름. */
    static Set<String> requiredNames(List<String> yamlTexts) {
        Map<String, Boolean> everUsable = new LinkedHashMap<>();
        for (String yaml : yamlTexts) {
            for (String body : placeholders(stripComments(yaml))) {
                Resolved r = analyze(body);
                if (!r.name.matches("[A-Z][A-Z0-9_]*")) {
                    continue;   // 스프링 프로퍼티 참조(${spring.profiles...})는 환경변수가 아니다
                }
                everUsable.merge(r.name, r.usableDefault, (a, b) -> a || b);
            }
        }
        Set<String> required = new LinkedHashSet<>();
        everUsable.forEach((name, usable) -> {
            if (!usable) {
                required.add(name);
            }
        });
        return required;
    }

    /** 템플릿이 선언한 키. {@code # KEY=} 처럼 주석 처리된 줄도 «보인다»고 본다. */
    static Set<String> templateKeys(String envExample) {
        Set<String> keys = new LinkedHashSet<>();
        for (String line : envExample.split("\n")) {
            String s = line.trim();
            if (s.startsWith("#")) {
                s = s.substring(1).trim();
            }
            int eq = s.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String key = s.substring(0, eq).trim();
            if (key.matches("[A-Za-z_][A-Za-z0-9_]*")) {
                keys.add(key);
            }
        }
        return keys;
    }

    /** YAML 주석 제거. 따옴표 안의 {@code #} 는 주석이 아니다. */
    static String stripComments(String yaml) {
        StringBuilder out = new StringBuilder();
        for (String line : yaml.split("\n", -1)) {
            char quote = 0;
            int cut = -1;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (quote != 0) {
                    if (c == quote) {
                        quote = 0;
                    }
                } else if (c == '"' || c == '\'') {
                    quote = c;
                } else if (c == '#' && (i == 0 || Character.isWhitespace(line.charAt(i - 1)))) {
                    cut = i;
                    break;
                }
            }
            out.append(cut < 0 ? line : line.substring(0, cut)).append('\n');
        }
        return out.toString();
    }

    /** {@code ${...}} 안쪽을 중괄호 균형을 맞춰 뽑는다 — 중첩 기본값 때문에 정규식으로는 모자란다. */
    static List<String> placeholders(String text) {
        List<String> bodies = new ArrayList<>();
        for (int i = 0; i + 1 < text.length(); i++) {
            if (text.charAt(i) != '$' || text.charAt(i + 1) != '{') {
                continue;
            }
            int depth = 0;
            int j = i;
            for (; j < text.length(); j++) {
                char c = text.charAt(j);
                if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        break;
                    }
                }
            }
            if (depth != 0) {
                continue;   // 닫히지 않은 것은 버린다
            }
            bodies.add(text.substring(i + 2, j));
            i = j;
        }
        return bodies;
    }

    private static Resolved analyze(String body) {
        int colon = body.indexOf(':');
        if (colon < 0) {
            return new Resolved(body.trim(), false);            // ${VAR} — 기동이 실패한다
        }
        String name = body.substring(0, colon).trim();
        String def = body.substring(colon + 1);
        if (def.trim().isEmpty()) {
            return new Resolved(name, false);                   // ${VAR:} — 조용히 망가진다
        }
        List<String> inner = placeholders(def);
        if (inner.size() == 1 && ("${" + inner.get(0) + "}").equals(def.trim())) {
            return new Resolved(name, analyze(inner.get(0)).usableDefault);   // ${A:${B:…}}
        }
        return new Resolved(name, true);
    }

    private static final class Resolved {
        final String name;
        final boolean usableDefault;

        Resolved(String name, boolean usableDefault) {
            this.name = name;
            this.usableDefault = usableDefault;
        }
    }
}
