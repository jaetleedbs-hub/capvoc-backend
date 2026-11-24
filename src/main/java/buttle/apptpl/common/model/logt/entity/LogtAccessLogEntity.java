package buttle.apptpl.common.model.logt.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 접속로그 — cdm_own.conn_log.
 *
 * <p>[N-18 되돌림, 2026-07-26] N-18 이 admin 로그를 공용 {@code chatcall_std} 로 옮기고
 * admin 의 {@code user_id} 를 표준의 {@code mbr_id} 칸에 억지로 끼워넣었던 것을 되돌린다.
 * admin 은 회원(mbr)이 아니라 {@code cdm_own.usr} 의 <b>usr_id/usr_nm</b> 을 신원으로 쓰므로,
 * admin 접속로그는 cdm_own.conn_log에 자기 신원으로 남긴다.
 *
 * <p>표준의 구조적 장점은 유지: 앱 생성 {@code log_id}(="alog-"+UUID) PK, {@code sys_cd}(='admin'),
 * timezone 시각, append-only(수정컬럼 없음). {@code log_id} 는 개인정보 접근이력의 부모키로 재사용.
 * 현재 구현의 log_id/sys_cd 필드는 기존 로깅 계약을 위해 유지한다.
 * 레거시(log_sid/prgrm_id 구조)는 {@code ${DB_SCHEMA}.logt_access_log_legacy} 로 보존.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conn_log", schema = "cdm_own")
public class LogtAccessLogEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    /** 로그 ID. 애플리케이션 생성("alog-"+UUID). */
    @Id
    @Column(name = "log_srno", length = 50, nullable = false)
    private String logId;

    /** 시스템 코드(admin). */
    @Column(name = "prgrm_id", length = 50)
    private String sysCd;

    /** 로그 일시. */
    @Column(name = "log_dttm")
    private LocalDateTime logDt;

    /** http 메소드. */
    @Column(name = "http_mthd_nm", length = 10)
    private String httpMethod;

    /** URL 주소. */
    @Column(name = "url_addr", length = 200)
    private String urlAddr;

    /** admin 사용자 ID (usr.usr_id). */
    @Column(name = "usr_id", length = 255)
    private String userId;

    // 이름 필드는 없다 [PII-4, 2026-08-31]. 컬럼(user_nm)은 아직 남아 있지만 이 엔티티가
    // 매핑하지 않으므로 새 행에는 채워지지 않는다. 조회 화면은 usr 를 조인해 이름을 얻는다.

    /** IP 주소. */
    @Column(name = "ip_addr", length = 15)
    private String ipAddr;
}
