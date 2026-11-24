package buttle.apptpl.common.security;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * [PII-6] UMS 수신자 연락처 백필을 <b>한 번</b> 돌린다.
 *
 * <p>기본값은 <b>꺼짐</b>이다. {@code --pii.backfill-ums-rcvr=true} 를 명시해야 돈다 —
 * 배포된 인스턴스가 재기동될 때 조용히 도는 일이 없어야 하므로, 설정 파일이 아니라
 * <b>실행 인자</b>를 본다(설정 파일은 실수로 커밋된다).
 *
 * <pre>
 *   ./gradlew bootRun --args='--pii.backfill-ums-rcvr=true --pii.backfill-exit=true'
 * </pre>
 *
 * <p>순서: V82(컬럼·검색키 확보) → 쓰기 경로 배포 → <b>이 백필</b>.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UmsRcvrContactBackfillRunner implements ApplicationRunner {

    private static final String FLAG = "pii.backfill-ums-rcvr";
    private static final String EXIT_FLAG = "pii.backfill-exit";

    private final UmsRcvrContactBackfillService service;

    @Override
    public void run(ApplicationArguments args) {
        if (!isTrue(args, FLAG)) {
            return;   // 평소 기동 — 아무 일도 하지 않는다
        }

        log.warn("[PII-6] 백필을 시작한다. 시작 전 상태: {}", service.status());
        log.warn("[PII-6] UMS 수신자 결과: {}", service.backfill());
        log.warn("[PII-6] 임직원·계약담당자 결과: {}", service.backfillStaffContacts());
        log.warn("[PII-6] ★ pending·userPending·cmpyPending 이 모두 0 이어야 끝난 것이다.");

        if (isTrue(args, EXIT_FLAG)) {
            log.warn("[PII-6] 백필 전용 기동이므로 종료한다.");
            System.exit(0);
        }
    }

    private static boolean isTrue(ApplicationArguments args, String name) {
        return args.containsOption(name)
                && args.getOptionValues(name).stream().anyMatch("true"::equalsIgnoreCase);
    }
}
