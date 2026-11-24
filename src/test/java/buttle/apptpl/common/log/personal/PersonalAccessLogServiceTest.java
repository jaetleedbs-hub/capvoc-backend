package buttle.apptpl.common.log.personal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * N-12 / W3-3 — 개인정보 접근 감사로그의 <저장 실패 삼킴 제거> 검증.
 *
 * <p>기존 구조는 DB 저장 실패를 catch 해 로그만 남기고 삼켰다(응답은 이미 반환됨 → 로그 없이
 * 개인정보 접근 성공, 무결성 미보장). 조치 후에는 실패를 <전용 감사-실패 채널>에 복구 가능한
 * 형태로 동기 기록한다. 이 테스트는 그 폴백이 실제로 남는지를 로거 이벤트로 실증한다.
 */
class PersonalAccessLogServiceTest {

    private LogtPersonalAccessLogRepo repo;
    private PersonalAccessLogService service;
    private Logger fallbackLogger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        repo = mock(LogtPersonalAccessLogRepo.class);
        service = new PersonalAccessLogService(repo,
                mock(buttle.apptpl.common.framework.config.EnvConfig.class));

        fallbackLogger = (Logger) LoggerFactory.getLogger("PERSONAL_ACCESS_AUDIT_FALLBACK");
        appender = new ListAppender<>();
        appender.start();
        fallbackLogger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        fallbackLogger.detachAppender(appender);
    }

    private LogtPersonalAccessLogEntity entity(String logId, int hstNo, String fld) {
        return LogtPersonalAccessLogEntity.builder()
                .logId(logId).hstNo(hstNo).grpNm("userdto").rowNo(0)
                .fldNm(fld).mskVal("홍*동").encVal("ENC(...)").mskTyp("NAME")
                .build();
    }

    @Test
    @DisplayName("DB 저장 실패 시 예외를 전파하지 않고 감사-실패 채널에 유실 항목을 남긴다")
    void recordsFallbackOnSaveFailure() {
        doThrow(new DataIntegrityViolationException("boom"))
                .when(repo).saveAll(anyList());

        List<LogtPersonalAccessLogEntity> entities = List.of(
                entity("alog-100", 0, "userNm"),
                entity("alog-100", 1, "telNo"));

        // 비동기 저장 본문은 예외를 밖으로 던지지 않아야 한다(호출부 안정성).
        assertDoesNotThrow(() -> service.saveLogAsync(entities));

        // 그러나 삼키지 않고 폴백 채널에 유실 2건이 ERROR 로 남아야 한다.
        long lossCount = appender.list.stream()
                .filter(e -> e.getLevel() == Level.ERROR)
                .filter(e -> e.getFormattedMessage().contains("AUDIT_LOSS"))
                .count();
        assertEquals(2, lossCount, "저장하려던 2건이 감사-실패 채널에 남아야 한다");

        // 복구에 필요한 식별자(logId/hstNo/필드)가 기록돼야 한다.
        assertTrue(appender.list.stream().anyMatch(
                e -> e.getFormattedMessage().contains("logId=alog-100")
                  && e.getFormattedMessage().contains("fld=userNm")),
                "유실 항목에 복구용 식별자가 포함돼야 한다");
    }

    @Test
    @DisplayName("빈 목록은 저장도 폴백도 하지 않는다")
    void skipsEmpty() {
        assertDoesNotThrow(() -> service.saveLogAsync(List.of()));
        assertTrue(appender.list.isEmpty(), "빈 목록엔 폴백 기록이 없어야 한다");
    }
}
