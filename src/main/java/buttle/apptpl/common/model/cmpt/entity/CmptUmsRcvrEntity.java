package buttle.apptpl.common.model.cmpt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;

import java.io.Serial;
import java.io.Serializable;

/**
 * UMS_수신자
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_ums_rcvr", schema = "cdm_own")
public class CmptUmsRcvrEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptUmsRcvrPkey id;

    /**
     * 회원사ID
     */
    @Column(name = "cmpy_id", nullable = false, length = 50)
    private String cmpyId;

    /**
     * 회원ID
     */
    @Column(name = "mbr_id", nullable = false, length = 50)
    private String mbrId;

    /**
     * 회원전화번호
     */
    @Column(name = "mbr_telno", nullable = true, length = 100)
    private String mbrTelno;

    /**
     * 회원이메일주소
     */
    @Column(name = "mbr_eml_addr", nullable = true, length = 320)
    private String mbrEmlAddr;

    /**
     * 회원권한코드
     */
    @Column(name = "mbr_authrt_cd", nullable = true, length = 20)
    private String mbrAuthrtCd;

    /**
     * 발송결과코드
     */
    @Column(name = "send_rslt_cd", nullable = true, length = 4)
    private String sendRsltCd;

    /**
     * 발송시작시간
     */
    @Column(name = "send_ymd", nullable = true, length = 8)
    private String sendYmd;

    /**
     * 발송결과시간
     */
    @Column(name = "send_tm", nullable = true, length = 6)
    private String sendTm;

    /**
     * 재시도횟수
     */
    @Column(name = "resend_cnt", nullable = true)
    private java.math.BigDecimal resendCnt;
}
