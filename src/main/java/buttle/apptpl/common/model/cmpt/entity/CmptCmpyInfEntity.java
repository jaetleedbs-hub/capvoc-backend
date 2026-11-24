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
 * 회원사정보
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_cmpy_inf")
public class CmptCmpyInfEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 회원사ID
     */
    @Id
    @Column(name = "cmpy_id", nullable = false, length = 50)
    private String cmpyId;

    /**
     * 회원사명
     */
    @Column(name = "cmpy_nm", nullable = true, length = 200)
    private String cmpyNm;

    /**
     * 회원사설명
     */
    @Column(name = "cmpy_desc", nullable = true, length = 4000)
    private String cmpyDesc;

    /**
     * 회원사 등록일시
     */
    @Column(name = "cmpy_reg_dt", nullable = true)
    private java.time.LocalDateTime cmpyRegDt;

    /**
     * 회원사 유형 코드
     */
    @Column(name = "cmpy_type_cd", nullable = true, length = 20)
    private String cmpyTypeCd;

    /**
     * 회원사 계약상태 코드
     */
    @Column(name = "cmpy_dlng_stts_cd", nullable = false, length = 20)
    private String cmpyDlngSttsCd;

    /**
     * 서버상태 시간
     */
    @Column(name = "svr_state_dt", nullable = false)
    private java.time.LocalDateTime svrStateDt;


    /**
     * 회원사 계약담당자명
     */
    @Column(name = "cntct_nm", nullable = true, length = 200)
    private String cntctNm;

    /**
     * 회원사 계약담당자 이메일
     */
    @Column(name = "cntct_email", nullable = true, length = 200)
    private String cntctEmail;

    /**
     * 회원사 계약담당자 전화번호
     */
    @Column(name = "cntct_phone", nullable = true, length = 20)
    private String cntctPhone;

    /**
     * API KEY
     */
    @Column(name = "api_key", nullable = true, length = 200)
    private String apiKey;

    /**
     * API 서버 URL
     */
    @Column(name = "api_svr_url", nullable = true, length = 100)
    private String apiSvrUrl;

    /**
     * API 인증 유형
     */
    @Column(name = "api_auth_type", nullable = true, length = 20)
    private String apiAuthType;

    /**
     * API 인증 토큰
     */
    @Column(name = "api_auth_token", nullable = true, length = 1000)
    private String apiAuthToken;

    /**
     * API 타임아웃(초)
     */
    @Column(name = "api_timeout_sec", nullable = true)
    private Integer apiTimeoutSec;
}
