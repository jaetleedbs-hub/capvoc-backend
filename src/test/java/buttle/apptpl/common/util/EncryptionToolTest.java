package buttle.apptpl.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 설정·필드 암호화 검증 [W2-3].
 *
 * <p>기존 테스트는 {@code smartcc.common.util} 패키지를 참조해 <b>컴파일조차 되지 않았고</b>
 * (corebase 이관 시 패키지가 옮겨졌으나 테스트가 따라가지 않음), 검증도 JVM 기본 설정에서
 * 비활성인 {@code assert} 문에 의존해 사실상 아무것도 확인하지 못했다.
 * JUnit 5 + 실제 단언으로 다시 쓴다.
 *
 * <p>이 클래스가 다루는 값은 {@code EntityFieldConverter}·{@code MybatisParameterInterceptor}를
 * 통해 <b>실제 DB 컬럼</b>에 적용된다.
 */
class EncryptionToolTest {

	private static final String KEY = "test-master-key-for-admin-0001";
	private static final String PLAIN = "010-1234-5678";

	@AfterEach
	void clearKeyProperty() {
		System.clearProperty("jasypt.encryptor.password");
	}

	// ---------------------------------------------------------------
	// W2-3 — 하드코딩 마스터키 폴백 제거
	// ---------------------------------------------------------------

	/**
	 * 기존에는 환경변수·시스템 프로퍼티가 없으면 저장소에 커밋된 {@code "aicc-default-key-2024"}를
	 * 조용히 돌려줬다. 즉 <b>설정 누락이 곧 알려진 키로 DB 필드를 암호화</b>하는 상태였다.
	 */
	@Test
	@DisplayName("마스터키가 설정되지 않으면 실패한다 — 알려진 기본키로 조용히 암호화하지 않는다")
	void failsWhenMasterKeyMissing() {
		System.clearProperty("jasypt.encryptor.password");

		// 환경변수 JASYPT_ENCRYPTOR_PASSWORD 가 설정된 환경에서는 이 검증이 성립하지 않는다.
		if (System.getenv("JASYPT_ENCRYPTOR_PASSWORD") != null
				&& !System.getenv("JASYPT_ENCRYPTOR_PASSWORD").trim().isEmpty()) {
			return;
		}

		IllegalStateException e = assertThrows(IllegalStateException.class, EncryptionTool::getDefaultKey);
		assertTrue(e.getMessage().contains("JASYPT_ENCRYPTOR_PASSWORD"),
				"조치 방법이 메시지에 드러나야 한다: " + e.getMessage());
	}

	/**
	 * 키 결정 순서는 <b>환경변수 → 시스템 프로퍼티 → 실패</b>다.
	 *
	 * <p>종전 이 테스트는 시스템 프로퍼티만 넣고 그 값이 나오길 기대했는데, 그것은
	 * <b>환경변수가 없다는 전제</b>에 기대고 있었다. 테스트 태스크에 {@code .env} 를 주입하자
	 * {@code JASYPT_ENCRYPTOR_PASSWORD} 가 보이면서 곧바로 깨졌다 — 코드가 틀린 게 아니라
	 * 테스트가 환경에 따라 결과가 달라지는 상태였다. 우선순위 자체를 검증하도록 바꾼다.
	 */
	@Test
	@DisplayName("키 결정 우선순위 — 환경변수가 있으면 환경변수, 없으면 시스템 프로퍼티")
	void resolvesKeyByDocumentedPrecedence() {
		System.setProperty("jasypt.encryptor.password", KEY);
		String env = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");

		if (env != null && !env.trim().isEmpty()) {
			assertEquals(env, EncryptionTool.getDefaultKey(),
					"환경변수가 시스템 프로퍼티보다 우선해야 한다");
		} else {
			assertEquals(KEY, EncryptionTool.getDefaultKey(),
					"환경변수가 없으면 시스템 프로퍼티를 써야 한다");
		}
	}

	// ---------------------------------------------------------------
	// 암복호 왕복 — 기존 데이터 호환 확인
	// ---------------------------------------------------------------

	@Test
	@DisplayName("일반 암호화는 왕복이 성립한다")
	void roundTripsStandardEncryption() {
		String encrypted = EncryptionTool.encrypt(PLAIN, KEY);

		assertNotEquals(PLAIN, encrypted);
		assertEquals(PLAIN, EncryptionTool.decrypt(encrypted, KEY));
	}

	/** {@code ENC(...)} 래핑은 설정파일용({@code *ForConfig}) 계열만 수행한다. */
	@Test
	@DisplayName("설정파일용 암호화만 ENC(...) 로 감싼다")
	void onlyConfigVariantWrapsWithEncMarker() {
		assertTrue(!EncryptionTool.isEncrypted(EncryptionTool.encrypt(PLAIN, KEY)));
		assertTrue(EncryptionTool.isEncrypted(EncryptionTool.encryptForConfig(PLAIN, KEY)));
		assertTrue(EncryptionTool.isEncrypted(EncryptionTool.encryptDeterministicForConfig(PLAIN, KEY)));
	}

	@Test
	@DisplayName("빈 값·키 누락은 거부한다")
	void rejectsEmptyInput() {
		assertThrows(IllegalArgumentException.class, () -> EncryptionTool.encrypt("", KEY));
		assertThrows(IllegalArgumentException.class, () -> EncryptionTool.encrypt(null, KEY));
		assertThrows(IllegalArgumentException.class, () -> EncryptionTool.encrypt(PLAIN, ""));
	}

	@Test
	@DisplayName("일반 암호화는 매번 다른 결과를 낸다 — 임의 salt")
	void standardEncryptionIsNonDeterministic() {
		assertNotEquals(EncryptionTool.encrypt(PLAIN, KEY), EncryptionTool.encrypt(PLAIN, KEY));
	}

	@Test
	@DisplayName("결정적 암호화는 왕복이 성립하고 한글·특수문자도 보존한다")
	void roundTripsDeterministicEncryption() {
		String[] samples = { PLAIN, "홍길동", "a1!@#$%^&*()_+-=[]{}|;':\",./<>?" };

		for (String sample : samples) {
			String encrypted = EncryptionTool.encryptDeterministic(sample, KEY);
			assertEquals(sample, EncryptionTool.decryptDeterministic(encrypted, KEY),
					"복호 결과가 원본과 달라졌다: " + sample);
		}
	}

	/**
	 * 결정적 암호화는 같은 평문이 항상 같은 암호문이 된다. 조회를 위해 의도된 성질이지만
	 * 빈도분석에 그대로 노출되므로 <b>전환 대상</b>이다(W2-6: blind index + 재암호화).
	 * 성질이 바뀌면 기존 데이터를 읽지 못하므로 여기에 고정해 둔다.
	 */
	@Test
	@DisplayName("결정적 암호화는 매번 같은 결과를 낸다 — 전환 전까지 유지되어야 하는 성질")
	void deterministicEncryptionIsStable() {
		String first = EncryptionTool.encryptDeterministic(PLAIN, KEY);
		String second = EncryptionTool.encryptDeterministic(PLAIN, KEY);
		String third = EncryptionTool.encryptDeterministic(PLAIN, KEY);

		assertEquals(first, second);
		assertEquals(second, third);
	}

	@Test
	@DisplayName("다른 키로 결정적 암호화하면 다른 값이 된다")
	void deterministicEncryptionDependsOnKey() {
		assertNotEquals(
				EncryptionTool.encryptDeterministic(PLAIN, KEY),
				EncryptionTool.encryptDeterministic(PLAIN, "another-master-key-0002"));
	}

	// ---------------------------------------------------------------
	// 필드 변환기 진입점 — 실제 DB 컬럼에 적용되는 경로
	// ---------------------------------------------------------------

	@Test
	@DisplayName("이미 암호화된 값은 다시 암호화하지 않는다 — 이중 암호화 방지")
	void doesNotDoubleEncrypt() {
		System.setProperty("jasypt.encryptor.password", KEY);

		String once = EncryptionTool.encryptField(PLAIN);
		String twice = EncryptionTool.encryptField(once);

		assertEquals(once, twice);
		assertEquals(PLAIN, EncryptionTool.decryptField(once));
	}

	@Test
	@DisplayName("평문을 복호 시도하면 그대로 돌려준다 — 미암호화 데이터 혼재 대응")
	void passesThroughPlainTextOnDecrypt() {
		System.setProperty("jasypt.encryptor.password", KEY);

		assertEquals(PLAIN, EncryptionTool.decryptField(PLAIN));
	}

	@Test
	@DisplayName("암호화 여부 판별은 ENC(...) 형태를 기준으로 한다")
	void detectsEncryptedForm() {
		assertTrue(EncryptionTool.isEncrypted("ENC(abc)"));
		assertTrue(!EncryptionTool.isEncrypted("abc"));
		assertTrue(!EncryptionTool.isEncrypted(null));
	}
}
