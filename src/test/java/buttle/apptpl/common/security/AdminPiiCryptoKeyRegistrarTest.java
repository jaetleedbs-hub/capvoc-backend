package buttle.apptpl.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import buttle.apptpl.common.framework.config.EnvConfig;
import micoai.corebase.pii.PiiCrypto;
import micoai.corebase.pii.PiiCryptoContext;

/**
 * 저장 암호화 키가 <b>실제로 등록되는가</b> — 안 되면 암호화가 조용히 평문 저장이 된다.
 *
 * <h3>왜 이 시험이 있는가 — 실제로 없었다 (2026-09-01 발견)</h3>
 * admin 은 corebase 를 컴포넌트 스캔하지 않아 corebase 의 키 등록기가 뜨지 않는다.
 * 그런데 <b>admin 쪽 등록기도 없었다.</b> corebase 주석은 "admin 은 자체적으로 호출한다"고
 * 적고 있었지만 그 호출이 어디에도 없었다.
 *
 * <p>그 상태에서 무슨 일이 생기는가 —
 * {@code PiiCrypto.encryptToDb} 는 키가 비면 <b>평문을 그대로 돌려준다</b>(키 미설정 환경 대비).
 * 그래서 매퍼에 암호화 typeHandler 를 붙여도 <b>평문이 저장되고, 예외도 로그도 없다.</b>
 * 조회 복호도 마찬가지로 무동작이라 화면에 {@code CCG1:} 가 그대로 나온다 — 실제로 그렇게 드러났다.
 *
 * <p>그래서 여기서는 <b>키 등록</b>만이 아니라 <b>암호화가 실제로 일어나는지</b>까지 본다.
 * 등록만 확인하면 "키는 넣었는데 암호화는 안 되는" 다음 형태를 놓친다.
 */
class AdminPiiCryptoKeyRegistrarTest {

    private static final String KEY = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    @AfterEach
    void clearKey() {
        // 정적 홀더다 — 다른 시험에 새지 않게 되돌린다.
        PiiCryptoContext.setKey(null);
    }

    private static AdminPiiCryptoKeyRegistrar registrarWith(String key) {
        EnvConfig env = new EnvConfig();
        env.setAesKey(key);
        return new AdminPiiCryptoKeyRegistrar(env);
    }

    @Test
    @DisplayName("★ 키가 등록되면 암호화가 <실제로> 일어난다 — 등록 확인만으로는 모자라다")
    void keyIsRegisteredAndEncryptionActuallyHappens() {
        PiiCryptoContext.setKey(null);
        registrarWith(KEY).register();

        assertEquals(KEY, PiiCryptoContext.getKey(), "키가 홀더에 들어가야 한다");

        String stored = PiiCrypto.encryptToDb("01012345678");
        assertTrue(stored.startsWith(PiiCrypto.GCM_PREFIX),
                "암호화 저장 형식(CCG1:)이어야 한다. 아니면 평문이 그대로 저장된 것이다: " + stored);
        assertNotEquals("01012345678", stored);
        assertEquals("01012345678", PiiCrypto.decryptFromDb(stored), "복호하면 원본으로 돌아와야 한다");
    }

    @Test
    @DisplayName("★ 키가 없으면 암호화가 <평문 저장>이 된다 — 이 시험이 그 위험을 문서로 남긴다")
    void withoutKeyEncryptionSilentlyStoresPlaintext() {
        PiiCryptoContext.setKey(null);

        String stored = PiiCrypto.encryptToDb("01012345678");

        // 이것이 정상 동작이다(키 미설정 환경 대비). 문제는 <조용하다>는 것이다 —
        // 그래서 등록기가 빠지면 아무도 모르게 평문이 쌓인다. 등록기는 선택이 아니다.
        assertEquals("01012345678", stored,
                "키가 없으면 평문이 그대로 저장된다 — corebase 의 의도된 동작이자, 등록기가 필요한 이유다");
    }

    @Test
    @DisplayName("키가 비어 있으면 등록기는 넘어가되 홀더는 비운다")
    void blankKeyIsRegisteredAsBlank() {
        PiiCryptoContext.setKey(KEY);
        registrarWith("").register();

        assertEquals("", PiiCryptoContext.getKey());
    }
}
