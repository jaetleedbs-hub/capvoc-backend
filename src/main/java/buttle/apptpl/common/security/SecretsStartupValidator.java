package buttle.apptpl.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 기동 시 시크릿 설정 검증.
 *
 * <p><b>이 검증기가 막는 것은 「설정을 안 했다」가 아니라 「설정을 안 했는데 떴다」이다.</b>
 * 시크릿에 하드코딩 기본값을 두면 값을 주지 않아도 앱이 조용히 뜬다 — 그러면
 * <b>설정 누락이 곧 알려진 키 사용</b>이 되고, 아무도 그것을 눈치채지 못한다.
 *
 * <p>두 가지를 본다.
 * <ol>
 *   <li><b>미설정</b> — 필수 시크릿이 비어 있는지</li>
 *   <li><b>유출 키 재사용</b> — 저장소에 커밋된 적이 있는 값을 그대로 쓰고 있는지</li>
 * </ol>
 *
 * <p>유출 키는 <b>지문(SHA-256 앞 8바이트)</b>으로만 비교한다. 검증 코드에 키 원본을 다시
 * 적으면 같은 유출을 반복하는 셈이기 때문이다.
 *
 * <p>동작은 프로파일에 따라 갈린다.
 * <ul>
 *   <li>운영·상용 프로파일: <b>기동 거부</b>(fail-fast)</li>
 *   <li>그 외(local/dev): 경고 후 기동 — 개발 흐름을 막지 않는다</li>
 * </ul>
 *
 * <p>★ <b>운영 프로파일을 새로 만들 때는 {@code security.secrets.fail-fast-profiles} 에
 * 그 이름을 추가한다.</b> 목록에 없으면 이 검증기는 <b>경고만 하고 통과시킨다</b> —
 * 즉 <b>있는데 아무것도 막지 않는</b> 상태가 된다. 기본 목록은
 * {@code prod,production,real,operation} 이다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecretsStartupValidator {

	private final Environment environment;

	/** 검사 대상: 설정 키 → 주입해야 할 환경변수명. */
	private static final Map<String, String> REQUIRED_SECRETS = new LinkedHashMap<>();

	static {
		REQUIRED_SECRETS.put("env.aes-key", "APPTPL_AES_KEY");
		REQUIRED_SECRETS.put("svc-env.jwt.secret", "APPTPL_JWT_SECRET");
		// [W2-3] Jasypt 마스터키.
		// 하드코딩 폴백을 제거한 뒤로 미설정이면 EncryptionTool.getDefaultKey() 가 예외를 던진다.
		// 그런데 그 시점이 <b>첫 사용 순간</b>이라, 검증에 넣지 않으면 앱은 정상 기동한 뒤
		// 개인정보 필드를 읽거나 쓰는 요청에서 비로소 실패한다. 기동 시점에 잡는다.
		REQUIRED_SECRETS.put("jasypt.encryptor.password", "JASYPT_ENCRYPTOR_PASSWORD");
	}

	/**
	 * 선택 기능의 시크릿: <b>설정되어 있으면</b> 유출 값인지만 본다.
	 *
	 * <p>메일 발송은 없어도 앱이 뜨므로 "미설정"을 위반으로 보면 개발 환경에 상시 경고가 쌓여
	 * 정작 봐야 할 위반이 묻힌다. 그러나 <b>유출된 값을 다시 주입하는 것</b>은
	 * 필수/선택과 무관하게 막아야 하므로 지문 검사 대상에는 포함한다.
	 */
	private static final Map<String, String> OPTIONAL_SECRETS = new LinkedHashMap<>();

	static {
		OPTIONAL_SECRETS.put("spring.mail.password", "MAIL_PASSWORD");
	}

	/**
	 * <b>저장소에 커밋된 적이 있는 값</b>들의 지문(SHA-256 앞 8바이트).
	 * 이 값이 감지되면 환경변수를 줬더라도 <b>이미 노출된 키</b>이므로 교체해야 한다.
	 *
	 * <p><b>템플릿에서는 비어 있다.</b> 채워야 하는 때가 반드시 온다 —
	 * 시크릿을 실수로 커밋했다가 걷어냈을 때다. 그때 <b>값을 지우는 것만으로는 부족하다</b>:
	 * git 이력에 남아 있고, 무엇보다 <b>아무도 그 값을 그만 쓰지 않는다.</b>
	 * 새 키로 바꾼 뒤 옛 값의 지문을 여기 넣으면, 누가 옛 값을 다시 주입했을 때 기동이 그것을 말한다.
	 *
	 * <p>지문 만드는 법 —
	 * <pre>{@code
	 *   ./gradlew fingerprint -Ptext='<옛 값>'
	 * }</pre>
	 *
	 * <p>★ <b>여기에 키 원본을 적지 않는다.</b> 그러면 같은 유출을 한 번 더 하는 것이다.
	 * 지문만 적는 이유가 그것이다.
	 */
	private static final Set<String> LEAKED_FINGERPRINTS = Set.of(
			// 예) "6209d09d89a8254b", // 2026-01-05 에 걷어낸 구 env.aes-key
	);

	/** 이 프로파일들에서는 위반 시 기동을 거부한다. */
	private static final String FAIL_FAST_PROFILES_KEY = "security.secrets.fail-fast-profiles";
	private static final String DEFAULT_FAIL_FAST_PROFILES = "prod,production,real,operation";

	@PostConstruct
	public void validate() {
		List<String> violations = new ArrayList<>();

		for (Map.Entry<String, String> entry : REQUIRED_SECRETS.entrySet()) {
			String propertyKey = entry.getKey();
			String envVarName = entry.getValue();
			String value = environment.getProperty(propertyKey);

			if (value == null || value.trim().isEmpty()) {
				violations.add(String.format(
						"[미설정] %s — 환경변수 %s 를 주입하십시오.", propertyKey, envVarName));
				continue;
			}

			checkLeaked(propertyKey, value, violations);
		}

		// 선택 시크릿: 설정돼 있을 때만 유출 여부를 본다.
		for (String propertyKey : OPTIONAL_SECRETS.keySet()) {
			String value = environment.getProperty(propertyKey);
			if (value != null && !value.trim().isEmpty()) {
				checkLeaked(propertyKey, value, violations);
			}
		}

		// 아래 둘은 <운영 프로파일에서만> 본다.
		//
		//  로컬에서 이것들은 «위반이 아니라 올바른 상태»다 — 스웨거를 켜고, 인가를 느슨히 두고,
		//  발송 수신자를 자기 주소로 돌려놓는 것이 로컬의 정상이다. 그런데도 여기서 함께 재면
		//  매 기동마다 거짓 ERROR 가 쌓이고, 그러면 <진짜 위반이 그 속에 묻힌다.>
		//  이 검증기가 OPTIONAL_SECRETS 에서 "미설정을 위반으로 보지 않는" 이유와 같다.
		//
		//  ★ 실제로 밟았다. `.env.example` 이 「반드시 자기 주소를 넣으라」고 시키는
		//    MAIL_OVERRIDE_TO 를 시킨 대로 넣으면, 그 순간부터 기동마다 ERROR 가 떴다 —
		//    <설정이 스스로 모순>이었다. 옳게 설정한 사람이 벌을 받으면 아무도 로그를 안 읽는다.
		if (isFailFastProfile()) {
			checkOverrideTo(violations);
			checkOpenByDefault(violations);
		}

		if (violations.isEmpty()) {
			log.info("시크릿 설정 검증 통과 — 필수 {}건 / 선택 {}건",
					REQUIRED_SECRETS.size(), OPTIONAL_SECRETS.size());
			return;
		}

		if (isFailFastProfile()) {
			String message = "시크릿 설정이 안전하지 않아 기동을 중단합니다.\n  - "
					+ String.join("\n  - ", violations);
			log.error(message);
			throw new IllegalStateException(message);
		}

		log.error("시크릿 설정에 문제가 있습니다. 운영 프로파일이었다면 기동이 거부됩니다.\n  - {}",
				String.join("\n  - ", violations));
	}

	/**
	 * 수신자 강제전환({@code *.override-to})이 <b>운영에 남아 있는지</b> 본다.
	 *
	 * <p>이 설정은 개발 안전장치다 — 값이 있으면 원래 수신자를 <b>전부 버리고</b> 그 주소로만 보낸다.
	 * 그래서 운영에 그대로 나가면 <b>전 고객의 비밀번호 재설정 링크·알림이 한 사서함으로 간다.</b>
	 * 설정 한 줄이 곧 전면 오배송이고, 받는 사람은 아무 일도 없는 것처럼 보이므로 늦게 발견된다.
	 *
	 * <p>다른 위반과 같은 규칙을 따른다 — 운영 프로파일이면 기동 거부, 그 외는 경고.
	 * 여기서 "무시하고 계속"을 택하지 않은 이유: 무시하면 <b>발송이 정상인 것처럼 보이는데</b>
	 * 설정과 실제 동작이 갈린다. 배포 시점에 크게 실패하는 편이 낫다.
	 */
	private void checkOverrideTo(List<String> violations) {
		String[] keys = { "app.mail.override-to", "app.sms.override-to" };
		for (String key : keys) {
			String value = environment.getProperty(key);
			if (value != null && !value.trim().isEmpty()) {
				violations.add(String.format(
						"[운영 금지 설정] %s — 수신자 강제전환이 켜져 있습니다(값 있음). "
						+ "운영에서는 전 고객 발송이 한 곳으로 갑니다. 이 설정을 제거하십시오.", key));
			}
		}
	}

	/**
	 * <b>기본값이 열려 있는 설정</b>이 운영에 그대로 나가는지 본다.
	 *
	 * <p>앞의 검사가 "값을 안 주면 <b>알려진 키</b>로 뜬다" 였다면 여기 넣는 셋은 결이 같다 —
	 * <b>값을 안 주면 열린 채로 뜬다.</b> 저장소에 운영 프로파일 설정을 두지 않는 것이 보통이므로
	 * <b>기본값이 곧 운영값</b>이 되고, 그래서 "아무도 켜지 않았는데 열려 있는" 상태가 만들어진다.
	 *
	 * <ul>
	 *   <li>{@code svc-env.api-auth-policy} — 기본 AUDIT. 매핑에 없는 API 를 <b>통과시키고 경고만</b> 남긴다</li>
	 *   <li>{@code app.internal-api-auth-policy} — 기본 AUDIT. 내부 서비스 간(S2S) 공유키 검증이 같은 상태가 된다.
	 *       게다가 {@code app.internal-api-key} 기본값이 <b>빈 문자열</b>이라, 둘이 겹치면 키 없이도 통과한다</li>
	 *   <li>{@code springdoc.api-docs.enabled} — springdoc 자체 기본이 <b>켜짐</b>이다
	 *       ({@code matchIfMissing = true}). {@code swagger-ui.enabled: false} 로는 안 닫힌다 —
	 *       별개 속성이라 화면만 꺼지고 {@code /v3/api-docs} 는 그대로 전체 엔드포인트 목록을 응답한다</li>
	 * </ul>
	 *
	 * <p><b>왜 앱 기본값을 ENFORCE 로 뒤집지 않았나.</b> 그러면 로컬과 프로파일 미지정 환경이
	 * 그날부터 매핑 없는 API 를 차단한다 — 새 화면을 만드는 동안 403 에 막히고, 남의 로컬을
	 * 조용히 깨뜨린다. 그래서 <b>기본값은 열어 두고, 운영에서만 기동을 거부</b>한다.
	 * 개발 흐름은 건드리지 않으면서 운영에는 새지 않는 자리다.
	 */
	private void checkOpenByDefault(List<String> violations) {
		requireEnforcing(violations, "svc-env.api-auth-policy",
				"관리콘솔 화면 인가(프로그램-권한 매핑)");
		requireEnforcing(violations, "app.internal-api-auth-policy",
				"내부 서비스 간(S2S) 공유키 검증");

		if (Boolean.parseBoolean(environment.getProperty("springdoc.api-docs.enabled", "false"))) {
			violations.add("[운영 금지 설정] springdoc.api-docs.enabled — API 문서가 켜져 있습니다. "
					+ "무인증으로 전체 엔드포인트 목록이 노출됩니다. 운영에서는 끄십시오.");
		}
	}

	/** 정책 값이 ENFORCE 가 아니면 위반으로 기록한다. 값이 없으면 <b>앱 기본값(AUDIT)</b>으로 뜬다는 뜻이다. */
	private void requireEnforcing(List<String> violations, String propertyKey, String what) {
		String value = environment.getProperty(propertyKey);
		if (value == null || value.trim().isEmpty()) {
			violations.add(String.format(
					"[기본값 노출] %s — 설정이 없습니다. 앱 기본값 AUDIT 으로 뜨므로 %s 가 <통과+경고> 가 됩니다. "
					+ "ENFORCE 를 명시하십시오.", propertyKey, what));
			return;
		}
		if (!"ENFORCE".equalsIgnoreCase(value.trim())) {
			violations.add(String.format(
					"[인가 미적용] %s = %s — %s 가 차단하지 않습니다. 운영은 ENFORCE 여야 합니다.",
					propertyKey, value.trim(), what));
		}
	}

	private boolean isFailFastProfile() {
		String configured = environment.getProperty(FAIL_FAST_PROFILES_KEY, DEFAULT_FAIL_FAST_PROFILES);
		List<String> failFast = Arrays.stream(configured.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.toList();

		return Arrays.stream(environment.getActiveProfiles())
				.anyMatch(active -> failFast.stream().anyMatch(active::equalsIgnoreCase));
	}

	/** 값이 유출 지문 목록에 걸리면 위반으로 기록한다. */
	private void checkLeaked(String propertyKey, String value, List<String> violations) {
		if (LEAKED_FINGERPRINTS.contains(fingerprint(value.trim()))) {
			violations.add(String.format(
					"[유출키 재사용] %s — 저장소에 커밋된 적이 있는 값입니다. 반드시 새 키로 교체하십시오(W1-5).",
					propertyKey));
		}
	}

	/** SHA-256 앞 8바이트를 hex로. 원본 키를 코드에 남기지 않기 위한 비교 수단이다. */
	private String fingerprint(String value) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256")
					.digest(value.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest, 0, 8);
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", e);
		}
	}
}
