package buttle.apptpl.common.failure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 장애 한 건을 <b>admin 자기 스키마</b>에 넣는다.
 *
 * <h3>왜 admin 이 자기 표에 쓰나</h3>
 * 「admin 로그는 admin 스키마, 통합은 배치로」가 이 저장소의 결정이다(N-18 뒤집힌 건).
 * 접속로그는 이미 그렇게 돌고 있었다 — {@code conn_log} 4,206행.
 * <b>오류만 writer 가 없어 0행이었다</b>(2026-09-10 실측). 이 매퍼가 그 빈자리를 채운다.
 *
 * <h3>⛔ 값을 다시 손보지 않는다</h3>
 * 자름·검증·마스킹은 {@code FailureRecorder} 가 <b>한 곳에서</b> 끝낸다.
 * 구현마다 다시 자르면 앱에 따라 길이가 달라져 <b>표를 못 믿게 된다.</b>
 * 여기는 <b>받은 값을 그대로 넣기만</b> 한다.
 *
 * <h3>⛔ 예외를 삼키지 않는다</h3>
 * 쓰기가 실패하면 <b>그대로 던진다.</b> {@code FailureRecorder} 가 받아 WARN 을 남긴다.
 * 여기서 삼키면 「기록됐다」가 거짓이 되고, 그것이 이 표가 없애려는 병이다.
 *
 * @since 2026-09-10
 */
@Mapper
public interface AdminFailureLogMapper {

    /**
     * @param svcCd      DESK·CONNECT·SEARCH·CHAT·ADMIN·BATCH (모르면 null)
     * @param svrId      {@code svr_inf.svr_id} (모르면 null — 배포가 주입하기 전까지 빈다)
     * @param cmpyId     요청 컨텍스트가 있을 때만 (모르면 null)
     * @param failKindCd EXCEPTION·DEPENDENCY·CONFIG·DEGRADED·REJECTED
     * @param subject    「무엇에 대해」 — 이미 200자 이내로 검증돼 온다
     * @param reason     사유 — 이미 4000자로 잘려 온다
     * @param errCd      예외 클래스명, 없으면 {@code failKindCd}
     * @param sevCd      ERROR · WARN
     * @param occurredAt 일어난 시각.
     *                   <p>⚠ <b>{@code Instant} 로 받는다 — {@code Date} 로 바꾸지 않는다.</b>
     *                   {@code FailureRecord} 가 2.1.37 에서 {@code Date} → {@code Instant} 로 바뀐 이유가
     *                   「어느 시간대로 읽을지를 <b>타입이</b> 닫아 둔다」였다. 어댑터에서 {@code Date} 로
     *                   되돌리면 그 닫은 것을 <b>다시 연다</b> — {@code Date} 는 시간대가 없어
     *                   JVM 기본값에 기댄다. 대상 칸도 {@code timestamptz} 라 바꿀 이유가 없다.
     */
    void insertFailure(@Param("svcCd") String svcCd,
                       @Param("svrId") String svrId,
                       @Param("cmpyId") String cmpyId,
                       @Param("failKindCd") String failKindCd,
                       @Param("subject") String subject,
                       @Param("reason") String reason,
                       @Param("errCd") String errCd,
                       @Param("sevCd") String sevCd,
                       @Param("occurredAt") java.time.Instant occurredAt);
}
