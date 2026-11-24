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
 * 서비스문의
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_srvc_qstn")
public class CmptSrvcQstnEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptSrvcQstnPkey id;

    /**
     * 문의분류코드
     */
    @Column(name = "srvc_qstn_clsf_cd", nullable = false, length = 20)
    private String srvcQstnClsfCd;

    /**
     * 질문내용
     */
    @Column(name = "srvc_qstn_cn", nullable = true, length = 50000)
    private String srvcQstnCn;

    /**
     * 답변내용
     */
    @Column(name = "srvc_ans_cn", nullable = true, length = 50000)
    private String srvcAnsCn;

    /**
     * 답변일시
     */
    @Column(name = "srvc_ans_dt", nullable = true)
    private java.time.LocalDateTime srvcAnsDt;

    /**
     * 비고
     */
    @Column(name = "srvc_bigo", nullable = true, length = 4000)
    private String srvcBigo;

    /**
     * 서비스문의접수여부
     */
    @Column(name = "srvc_qstn_rcpt_yn", nullable = true, length = 1)
    private String srvcQstnRcptYn;

    /**
     * 질문자명
     */
    @Column(name = "svc_qstn_nm", nullable = true, length = 50)
    private String svcQstnNm;

    /**
     * 답변자명
     */
    @Column(name = "svc_ans_nm", nullable = true, length = 50)
    private String svcAnsNm;
}
