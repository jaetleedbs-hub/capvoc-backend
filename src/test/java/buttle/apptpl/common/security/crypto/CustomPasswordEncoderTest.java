package buttle.apptpl.common.security.crypto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 비밀번호 해시 검증.
 *
 * <p>여기서 지키는 것은 「BCrypt 가 동작한다」가 아니라 <b>「BCrypt <i>만</i> 통과한다」</b>이다.
 * 앞의 것은 라이브러리가 이미 보장하고, 뒤의 것은 <b>우리가 실수로 무너뜨릴 수 있는 것</b>이다 —
 * 누가 「옛 데이터 때문에」 폴백을 한 줄 넣으면 그날부터 빠른 해시가 영구히 통과한다.
 * 아래 <b>음성 대조</b>가 그 순간 빨갛게 된다.
 */
class CustomPasswordEncoderTest {

	private static final String RAW = "Adm1n!Password#2026";

	private CustomPasswordEncoder encoder;

	@BeforeEach
	void setUp() {
		encoder = new CustomPasswordEncoder(4);   // 시험은 낮은 강도로 — 판정에는 영향이 없고 빠르다
	}

	@Test
	@DisplayName("저장은 BCrypt 로 한다")
	void encodesWithBcrypt() {
		String encoded = encoder.encode(RAW);

		assertTrue(encoded.startsWith("$2"), "BCrypt 해시여야 한다: " + encoded);
		assertTrue(encoder.matches(RAW, encoded));
	}

	@Test
	@DisplayName("같은 비밀번호라도 저장할 때마다 해시가 다르다 — 솔트가 붙는다")
	void bcryptIsSalted() {
		assertFalse(encoder.encode(RAW).equals(encoder.encode(RAW)));
	}

	@Test
	@DisplayName("강도를 올려도 옛 강도로 만든 해시는 그대로 검증된다")
	void oldStrengthStillVerifies() {
		String madeWeak = new CustomPasswordEncoder(4).encode(RAW);

		assertTrue(new CustomPasswordEncoder(6).matches(RAW, madeWeak),
				"강도는 해시 문자열 안에 들어 있다 — 올려도 기존 계정이 잠기지 않는다");
	}

	// ── 음성 대조 ────────────────────────────────────────────────────────────
	//  「통과했다」만으로는 판정이 살아 있는지 알 수 없다. 무엇을 <막는지>를 여기서 못박는다.

	@Test
	@DisplayName("음성 대조: 틀린 비밀번호는 통과하지 않는다")
	void rejectsWrongPassword() {
		assertFalse(encoder.matches("wrong", encoder.encode(RAW)));
	}

	@Test
	@DisplayName("음성 대조: 평문이 저장돼 있어도 통과하지 않는다")
	void rejectsPlainTextStored() {
		assertFalse(encoder.matches(RAW, RAW));
	}

	@Test
	@DisplayName("음성 대조: SHA-512 해시는 통과하지 않는다 — 빠른 해시 폴백 금지")
	void rejectsFastHashes() throws Exception {
		assertFalse(encoder.matches(RAW, unsaltedSha512(RAW)),
				"무솔트 SHA-512 가 통과하면, 누가 폴백을 되살린 것이다");
		assertFalse(encoder.matches(RAW, "c2FsdA==$" + unsaltedSha512(RAW)),
				"솔트를 붙였어도 SHA-512 는 빠른 해시다 — 비밀번호 저장에 쓰지 않는다");
	}

	@Test
	@DisplayName("음성 대조: null·빈 값은 통과하지 않는다")
	void rejectsNullAndEmpty() {
		assertFalse(encoder.matches(RAW, null));
		assertFalse(encoder.matches(RAW, ""));
	}

	@Test
	@DisplayName("BCrypt 해시는 재인코딩 대상이 아니다 — 로그인마다 재해싱되지 않는다")
	void reportsNoUpgradeForBcrypt() {
		assertFalse(encoder.upgradeEncoding(encoder.encode(RAW)));
	}

	/** {@code hex(sha512(password))} — 「옛 형식」의 대표로 쓴다. */
	private String unsaltedSha512(String raw) throws Exception {
		byte[] bytes = MessageDigest.getInstance("SHA-512").digest(raw.getBytes(StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
