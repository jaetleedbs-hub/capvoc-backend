package buttle.apptpl.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

/**
 * [보안조사 2026-09-04] <b>기본값이 열려 있는 설정</b>이 운영으로 나가는 것을 기동에서 막는지 본다.
 *
 * <p>무엇을 지키려는 것인가 — 저장소에 <b>운영 프로파일 설정이 없다</b>(config-hub 에 dev·demo 뿐).
 * 그래서 운영은 앱 기본값으로 뜨는데, 그 기본값이 셋이나 <b>열린 쪽</b>이다.
 * <ul>
 *   <li>{@code svc-env.api-auth-policy} 기본 AUDIT — 매핑에 없는 API 를 통과시키고 경고만 남긴다</li>
 *   <li>{@code app.internal-api-auth-policy} 기본 AUDIT — 게다가 공유키 기본값이 <b>빈 문자열</b>이다</li>
 *   <li>{@code springdoc.api-docs.enabled} — springdoc 자체 기본이 켜짐이라
 *       {@code swagger-ui.enabled: false} 로는 안 닫힌다(별개 속성)</li>
 * </ul>
 *
 * <p><b>이 시험이 실제로 무엇을 잡는지</b>도 함께 못박는다. 마지막 시험은 <b>대조군</b>이다 —
 * 운영이 아닌 프로파일에서는 같은 설정이 위반이 아니어야 한다. 그것이 통과해야
 * "prod 에서만 막는다"가 참이 되고, 아니면 이 검사는 dev 를 깨뜨리는 검사일 뿐이다.
 */
class SecretsStartupValidatorProdGateTest {

	/** 필수 시크릿은 채워 둔다 — 그래야 위반이 뜨면 <이번에 넣은 검사 때문>임이 분명해진다. */
	private MockEnvironment baseEnv(String... activeProfiles) {
		MockEnvironment env = new MockEnvironment();
		env.setActiveProfiles(activeProfiles);
		env.setProperty("env.aes-key", "test-aes-key-not-leaked-0904");
		env.setProperty("svc-env.jwt.secret", "test-jwt-secret-not-leaked-0904");
		env.setProperty("jasypt.encryptor.password", "test-jasypt-not-leaked-0904");
		return env;
	}

	/** 운영에서 <제대로 잠근> 설정. 이 상태는 기동해야 한다. */
	private MockEnvironment lockedDownProd() {
		MockEnvironment env = baseEnv("prod");
		env.setProperty("svc-env.api-auth-policy", "ENFORCE");
		env.setProperty("app.internal-api-auth-policy", "ENFORCE");
		env.setProperty("springdoc.api-docs.enabled", "false");
		return env;
	}

	@Test
	@DisplayName("운영: 인가 정책이 설정돼 있지 않으면 기동을 거부한다 — 기본값 AUDIT 으로 뜨기 때문")
	void prodRefusesWhenAuthPolicyUnset() {
		MockEnvironment env = baseEnv("prod");
		env.setProperty("springdoc.api-docs.enabled", "false");
		// api-auth-policy · internal-api-auth-policy 를 <일부러 주지 않는다> = 운영의 실제 상태

		assertThatThrownBy(() -> new SecretsStartupValidator(env).validate())
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("svc-env.api-auth-policy")
				.hasMessageContaining("app.internal-api-auth-policy");
	}

	@Test
	@DisplayName("운영: 인가 정책이 AUDIT 이면 기동을 거부한다 — 통과+경고는 차단이 아니다")
	void prodRefusesAuditPolicy() {
		MockEnvironment env = lockedDownProd();
		env.setProperty("svc-env.api-auth-policy", "AUDIT");

		assertThatThrownBy(() -> new SecretsStartupValidator(env).validate())
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("svc-env.api-auth-policy");
	}

	@Test
	@DisplayName("운영: API 문서가 켜져 있으면 기동을 거부한다 — 무인증으로 전체 엔드포인트가 드러난다")
	void prodRefusesApiDocsEnabled() {
		MockEnvironment env = lockedDownProd();
		env.setProperty("springdoc.api-docs.enabled", "true");

		assertThatThrownBy(() -> new SecretsStartupValidator(env).validate())
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("springdoc.api-docs.enabled");
	}

	@Test
	@DisplayName("운영: 셋 다 잠갔으면 기동한다 — 검사가 <잠근 상태까지> 막으면 쓸 수 없다")
	void prodStartsWhenLockedDown() {
		assertThatCode(() -> new SecretsStartupValidator(lockedDownProd()).validate())
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("대조군 — dev 는 스웨거를 켜고 정책이 없어도 기동한다")
	void devIsNotAffected() {
		MockEnvironment env = baseEnv("dev");
		env.setProperty("springdoc.api-docs.enabled", "true");
		// 정책 둘은 주지 않는다 — dev 에서 이게 위반이 되면 매 기동마다 거짓 경고가 쌓인다

		assertThatCode(() -> new SecretsStartupValidator(env).validate())
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("fail-fast 프로파일 목록은 설정으로 바꿀 수 있다 — 운영 프로파일 이름이 늘어날 때를 위해")
	void failFastProfileListIsConfigurable() {
		MockEnvironment env = baseEnv("staging");
		env.setProperty("security.secrets.fail-fast-profiles", "staging");
		env.setProperty("springdoc.api-docs.enabled", "true");

		assertThatThrownBy(() -> new SecretsStartupValidator(env).validate())
				.isInstanceOf(IllegalStateException.class);

		// 같은 설정이라도 목록에 없으면 통과한다(대조)
		MockEnvironment loose = baseEnv("staging");
		loose.setProperty("springdoc.api-docs.enabled", "true");
		assertThatCode(() -> new SecretsStartupValidator(loose).validate())
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("메시지에 <무엇을 하라>가 들어 있다 — 기동이 막힌 사람이 다음 행동을 알 수 있게")
	void messageTellsWhatToDo() {
		MockEnvironment env = baseEnv("prod");
		env.setProperty("springdoc.api-docs.enabled", "true");

		assertThatThrownBy(() -> new SecretsStartupValidator(env).validate())
				.isInstanceOf(IllegalStateException.class)
				.satisfies(e -> assertThat(e.getMessage())
						.contains("ENFORCE")
						.contains("끄십시오"));
	}
}
