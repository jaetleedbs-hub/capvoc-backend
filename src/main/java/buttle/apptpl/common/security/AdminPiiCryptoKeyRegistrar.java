package buttle.apptpl.common.security;

import org.springframework.stereotype.Component;

import buttle.apptpl.common.framework.config.EnvConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.pii.PiiCryptoContext;

/**
 * 저장 암호화 키를 {@link PiiCryptoContext} 에 등록한다 — <b>admin 전용</b>.
 *
 * <h3>왜 admin 에만 따로 있는가</h3>
 * corebase 의 {@code DefaultPiiCryptoKeyRegistrar} 는 corebase 의 {@code EnvConfig} 를 주입받는데,
 * admin 은 corebase 패키지를 컴포넌트 스캔하지 않고 <b>자기 {@code EnvConfig}</b>(env.aes-key)를 쓴다.
 * 그래서 그 등록기가 admin 에서는 뜨지 않는다.
 *
 * <h3>⚠ 없으면 무엇이 조용히 깨지는가 (2026-09-01 실측)</h3>
 * {@code PiiCryptoContext} 가 비어 있으면 —
 *
 * <ul>
 *   <li>{@code PiiCrypto.encryptToDb} 가 <b>평문을 그대로 돌려준다</b>(키 미설정 환경 대비 동작).
 *       즉 매퍼에 {@code PiiGcmTypeHandler} 를 붙여도 <b>평문이 저장된다</b> — 예외도 로그도 없다</li>
 *   <li>{@code PiiCrypto.decryptFromDb} 도 그대로 돌려준다. 그래서 조회 복호 인터셉터가
 *       <b>붙어 있는데도</b> 화면에 {@code CCG1:…} 가 그대로 보인다</li>
 * </ul>
 *
 * 실제로 그랬다 — 회원사 목록의 담당자 연락처가 암호문으로 화면에 나왔고,
 * 그 원인을 좇아 이 등록기가 <b>애초에 없었다</b>는 것이 드러났다.
 * corebase 쪽 주석은 "admin 은 자체적으로 호출한다"고 적고 있었지만, 그 호출이 없었다.
 *
 * <p>admin 자체 코드가 {@code envConfig.getAesKey()} 를 <b>직접</b> 넘겨 쓰는 곳들
 * ({@code AdminPiiAuditLogger}·{@code PersonalAccessLogService})은 이 홀더를 거치지 않아
 * 정상 동작했다. 그래서 <b>일부는 되고 일부는 안 되는</b> 상태가 오래 눈에 띄지 않았다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPiiCryptoKeyRegistrar {

    private final EnvConfig envConfig;

    @PostConstruct
    public void register() {
        String key = envConfig.getAesKey();
        PiiCryptoContext.setKey(key);
        if (key == null || key.isEmpty()) {
            // 조용히 넘기지 않는다 — 이 상태에서는 암호화 typeHandler 가 평문을 저장한다.
            log.error("[PII] 저장 암호화 키가 비어 있다 — 암호화 저장이 <평문 저장>이 된다."
                    + " env.aes-key 설정을 확인할 것.");
        } else {
            log.info("[PII] 저장 암호화 키 등록 — 암복호 활성(키 설정됨)");
        }
    }
}
