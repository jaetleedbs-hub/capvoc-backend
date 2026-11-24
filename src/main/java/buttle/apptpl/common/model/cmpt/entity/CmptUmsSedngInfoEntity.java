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
 * UMS_발송정보
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_ums_sedng_info")
public class CmptUmsSedngInfoEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * UMSID
     */
    @Id
    @Column(name = "ums_id", nullable = false, length = 50)
    private String id;

    /**
     * 접수일자
     */
    @Column(name = "acct_ymd", nullable = true, length = 8)
    private String acctYmd;

    /**
     * 접수시간
     */
    @Column(name = "acct_tm", nullable = true, length = 6)
    private String acctTm;

    /**
     * UMS제목
     */
    @Column(name = "ums_ttl", nullable = true, length = 1000)
    private String umsTtl;

    /**
     * UMS내용
     */
    @Column(name = "ums_cn", nullable = true)
    private String umsCn;

    /**
     * 사용자ID
     */
    @Column(name = "user_id", nullable = true, length = 50)
    private String userId;

    /**
     * 대량발송번호
     */
    @Column(name = "bulk_send_no", nullable = true, length = 20)
    private String bulkSendNo;

    /**
     * 발송방법코드
     */
    @Column(name = "send_way_cd", nullable = true, length = 20)
    private String sendWayCd;

    /**
     * 예약구분코드
     */
    @Column(name = "res_se_cd", nullable = true, length = 20)
    private String resSeCd;

    /**
     * 발송요청일자
     */
    @Column(name = "send_req_ymd", nullable = true, length = 8)
    private String sendReqYmd;

    /**
     * 발송요청시간
     */
    @Column(name = "send_req_tm", nullable = true, length = 6)
    private String sendReqTm;

    /**
     * 고객ID
     */
    @Column(name = "cust_id", nullable = true, length = 50)
    private String custId;

    /**
     * 발송일자
     */
    @Column(name = "send_ymd", nullable = true, length = 8)
    private String sendYmd;

    /**
     * 발송시간
     */
    @Column(name = "send_tm", nullable = true, length = 6)
    private String sendTm;

    /**
     * 재발송여부
     */
    @Column(name = "resend_yn", nullable = true, length = 1)
    private String resendYn;

    /**
     * 발송자IP
     */
    @Column(name = "send_ip", nullable = true, length = 20)
    private String sendIp;

    /**
     * 발송결과코드
     */
    @Column(name = "send_rslt_cd", nullable = true, length = 4)
    private String sendRsltCd;

    /**
     * 발송결과메시지
     */
    @Column(name = "send_rslt_msg", nullable = true, length = 1000)
    private String sendRsltMsg;

    /**
     * 발신번호
     */
    @Column(name = "sndr_telno", nullable = true, length = 20)
    private String sndrTelno;

    /**
     * 수신자 이름
     */
    @Column(name = "rcvr_nm", nullable = true, length = 10)
    private String rcvrNm;

    /**
     * 수신자 전화번호
     */
    @Column(name = "rcvr_telno", nullable = true, length = 20)
    private String rcvrTelno;

    /**
     * 문자서식ID
     */
    @Column(name = "tmplt_id", nullable = true, length = 50)
    private String tmpltId;

    /**
     * 발송 대상 코드
     */
    @Column(name = "send_target_cd", nullable = true, length = 20)
    private String sendTargetCd;

    /**
     * 대량발송그룹
     */
    @Column(name = "bulk_send_group", nullable = true, length = 50)
    private String bulkSendGroup;

    /**
     * 문자구분
     */
    @Column(name = "ums_se", nullable = true, length = 20)
    private String umsSe;

    /**
     * 재발송일자
     */
    @Column(name = "resend_ymd", nullable = true, length = 8)
    private String resendYmd;

    /**
     * 재발송시간
     */
    @Column(name = "resend_tm", nullable = true, length = 6)
    private String resendTm;
}
