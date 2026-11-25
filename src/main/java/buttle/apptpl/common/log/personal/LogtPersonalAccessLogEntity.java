package buttle.apptpl.common.log.personal;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 개인정보 접근이력 — [N-18] 표준 스키마 단일화.
 *
 * <p>[N-18 되돌림, 2026-07-26] admin 자기 스키마({@code ${DB_SCHEMA}.log_personal_access_log})에
 * 기록한다(독립 스키마). 구조는 표준과 동일(부모키 = 접속로그의 {@code log_id} 문자열, mbr 신원 없음).
 */
@Entity
@Table(name = "log_personal_access_log", schema = "cdm_own")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(LogtPersonalAccessLogPkey.class)
public class LogtPersonalAccessLogEntity {

    /** 부모 접속로그 log_id. */
    @Id
    private String logId;

    /** 접근이력 순번. */
    @Id
    private Integer hstNo;

    private String grpNm;

    private Integer rowNo;

    private String fldNm;

    private String mskVal;

    private String encVal;

    private String mskTyp;

    /** 등록자(현재 사용자 lgn_id). */
    private String rgtrId;

    /** 등록 일시. */
    private LocalDateTime regDt;
}
