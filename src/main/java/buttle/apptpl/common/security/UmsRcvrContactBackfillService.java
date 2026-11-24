package buttle.apptpl.common.security;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import buttle.apptpl.common.framework.config.EnvConfig;
import buttle.apptpl.common.security.mapper.UmsRcvrContactBackfillMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.util.CoreUtils.aes256;

/**
 * [PII-6] UMS 수신자 연락처를 <b>GCM + 검색 키</b>로 옮기는 일회성 백필.
 *
 * <h3>★ 쓰기 경로 배포 <b>뒤에</b> 돌린다</h3>
 * 순서를 뒤집으면 백필이 끝난 뒤 들어온 행이 평문으로 남고, 아무도 모른다.
 * 이 프로젝트는 그 실수를 이미 두 번 했다({@code cust_nm_enc}·{@code cust_zip_addr_enc} —
 * 둘 다 배치로만 채우고 쓰기 경로를 안 붙여 <b>절반만 채워진 컬럼</b>이 됐다).
 *
 * <h3>멱등하다</h3>
 * 대상은 「내용이 있는데 아직 GCM 이 아니다」 또는 「GCM 인데 검색 키가 비었다」이다.
 * 여러 번 돌려도 안전하다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmsRcvrContactBackfillService {

    /** 한 번에 처리할 행 수. 트랜잭션을 길게 끌지 않는다. */
    private static final int PAGE = 200;

    /** 무한 루프 방지. 넘기면 <b>갱신이 조건을 벗어나지 못하는 것</b>이다. */
    private static final int MAX_PAGES = 200;

    private final UmsRcvrContactBackfillMapper mapper;
    private final UmsRcvrContactKey contactKey;
    private final EnvConfig envConfig;

    /** 진행 전/후 확인용. 셋 다 0 이어야 끝난 것이다. */
    public Map<String, Object> status() {
        Map<String, Object> out = new LinkedHashMap<>();
        int pending = mapper.countPending();
        int userPending = mapper.countUserPending();
        int cmpyPending = mapper.countCmpyPending();
        out.put("total", mapper.countTotal());
        out.put("pending", pending);
        out.put("userPending", userPending);
        out.put("cmpyPending", cmpyPending);
        out.put("done", pending == 0 && userPending == 0 && cmpyPending == 0);
        return out;
    }

    /**
     * [PII-6 잔여] 임직원·계약담당자 연락처를 GCM 으로 옮긴다.
     *
     * <p>UMS 수신자와 달리 <b>검색 키를 만들지 않는다</b> — 그 두 표에는 조회 술어가 0곳이다
     * (매퍼 전수 확인). 없는 것을 만들면 쓰는 사람 없는 컬럼이 하나 더 생길 뿐이다.
     *
     * <p>표가 작아(임직원 3행·회원사 1행) 페이지 루프를 두지 않는다. 대신 <b>끝나고 남은 수를
     * 다시 세어</b> 진짜로 줄었는지 확인한다 — 「갱신이 성공했는가」는 진전의 증거가 아니라는 것을
     * 이 트랙에서 두 번 배웠다.
     */
    @Transactional
    public Map<String, Object> backfillStaffContacts() {
        int filled = 0;
        int skipped = 0;

        for (Map<String, Object> row : mapper.selectUserPending(PAGE)) {
            String userId = str(row.get("userId"));
            try {
                int n = mapper.updateUserOne(userId,
                        encrypt(toPlain(str(row.get("mblTelNo")))),
                        encrypt(toPlain(str(row.get("homeTelNo")))),
                        encrypt(toPlain(str(row.get("emlAddr")))));
                if (n == 1) {
                    filled++;
                } else {
                    skipped++;
                    log.error("[PII-6] 임직원 갱신이 1행이 아니다 — user={} 영향행={}", userId, n);
                }
            } catch (RuntimeException e) {
                skipped++;
                log.error("[PII-6] 임직원 처리 실패 — user={} : {}", userId, e.getMessage());
            }
        }

        for (Map<String, Object> row : mapper.selectCmpyPending(PAGE)) {
            String cmpyId = str(row.get("cmpyId"));
            try {
                int n = mapper.updateCmpyOne(cmpyId,
                        encrypt(toPlain(str(row.get("cntctPhone")))),
                        encrypt(toPlain(str(row.get("cntctEmail")))));
                if (n == 1) {
                    filled++;
                } else {
                    skipped++;
                    log.error("[PII-6] 회원사 갱신이 1행이 아니다 — cmpy={} 영향행={}", cmpyId, n);
                }
            } catch (RuntimeException e) {
                skipped++;
                log.error("[PII-6] 회원사 처리 실패 — cmpy={} : {}", cmpyId, e.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("filled", filled);
        result.put("skipped", skipped);
        result.put("status", status());
        log.info("[PII-6] 임직원·계약담당자 연락처 백필 — 채움 {} · 건너뜀 {} · 남은 임직원 {} · 남은 회원사 {}",
                filled, skipped, mapper.countUserPending(), mapper.countCmpyPending());
        return result;
    }

    @Transactional
    public Map<String, Object> backfill() {
        int filled = 0;
        int skipped = 0;
        int pages = 0;
        int remainingBefore = mapper.countPending();

        while (pages++ < MAX_PAGES) {
            List<Map<String, Object>> rows = mapper.selectPending(PAGE);
            if (rows.isEmpty()) {
                break;
            }

            for (Map<String, Object> row : rows) {
                String umsId = str(row.get("umsId"));
                Integer umsSn = row.get("umsSn") == null ? null : Integer.valueOf(String.valueOf(row.get("umsSn")));
                try {
                    // 어떤 형태로 들어오든 평문으로 되돌린 뒤 GCM·검색키를 만든다.
                    // 안 벗기면 암호문을 다시 암호화하고, 검색 키는 암호문을 정규화한 값이 되어
                    // <예외 없이 0건>이 된다.
                    String telnoPlain = toPlain(str(row.get("mbrTelno")));
                    String emailPlain = toPlain(str(row.get("mbrEmlAddr")));

                    int n = mapper.updateOne(umsId, umsSn,
                            encrypt(telnoPlain), contactKey.telno(telnoPlain),
                            encrypt(emailPlain), contactKey.email(emailPlain));
                    // 영향행을 <센다>. 세지 않아 「정정 1건」이라는 거짓 보고를 한 전례가 있다.
                    if (n == 1) {
                        filled++;
                    } else {
                        skipped++;
                        log.error("[PII-6] 갱신이 1행이 아니다 — ums={} sn={} 영향행={}", umsId, umsSn, n);
                    }
                } catch (RuntimeException e) {
                    // 한 행의 문제로 나머지를 멈추지 않는다. 다만 조용히 넘기지도 않는다.
                    skipped++;
                    log.error("[PII-6] 처리 실패 — ums={} sn={} : {}", umsId, umsSn, e.getMessage());
                }
            }

            // ★ 진전의 기준은 「갱신이 성공했는가」가 아니라 「남은 대상이 줄었는가」다.
            //   그 둘은 다르다 — 갱신은 1행씩 성공하는데 값이 조건을 벗어나지 못할 수 있다.
            //   PII-2 에서 실제로 그랬고, 성공 건수만 보면 <일하고 있는 것처럼> 보였다.
            int remainingNow = mapper.countPending();
            if (remainingNow >= remainingBefore) {
                log.error("[PII-6] 진전이 없다 — 남은 대상 {} → {}. 같은 행을 다시 잡지 않도록 멈춘다.",
                        remainingBefore, remainingNow);
                break;
            }
            remainingBefore = remainingNow;
        }

        if (pages >= MAX_PAGES) {
            log.error("[PII-6] 백필이 상한에 걸렸다 — 갱신이 반영되지 않는 것일 수 있다.");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("filled", filled);
        result.put("skipped", skipped);
        result.put("status", status());
        log.info("[PII-6] UMS 수신자 연락처 백필 — 채움 {} · 건너뜀 {} · 남은 대상 {}",
                filled, skipped, mapper.countPending());
        return result;
    }

    /**
     * GCM · 레거시 CBC · 평문 어느 것이 와도 <b>평문</b>으로 되돌린다.
     *
     * <p>{@code aes256.decrypt} 는 {@code CCG1:} 접두면 GCM 으로, 아니면 레거시 CBC 로 푼다.
     * 어느 쪽도 아니면(=이미 평문) {@code null} 을 돌려주므로 원문을 그대로 쓴다.
     */
    private String toPlain(String stored) {
        if (stored == null || stored.trim().isEmpty()) {
            return stored;
        }
        String decrypted = aes256.decrypt(stored, envConfig.getAesKey());
        return decrypted != null ? decrypted : stored;
    }

    /** 평문을 저장 형태(GCM)로. 빈 값은 그대로 — 암호화할 것이 없다. */
    private String encrypt(String plain) {
        if (plain == null || plain.trim().isEmpty()) {
            return plain;
        }
        return aes256.encryptGcm(plain, envConfig.getAesKey());
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
