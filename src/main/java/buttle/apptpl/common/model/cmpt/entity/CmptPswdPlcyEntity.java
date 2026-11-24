package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;

/**
 * 비밀번호 정책관리
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_pswd_plcy")
public class CmptPswdPlcyEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 비밀번호정책아이디
     */
    @Id
    @Column(name = "pswd_plcy_id", nullable = false)
    private BigDecimal pswdPlcyId;
    
    /**
     * 비밀번호 변경 주기 수
     */
    @Column(name = "pswd_chg_cyc_cnt")
    private BigDecimal pswdChgCycCnt;

    /**
     * 로그인 시도 가능 수
     */
    @Column(name = "lgn_try_psblty_cnt")
    private BigDecimal lgnTryPsbltyCnt;

    /**
     * 비밀번호 재사용 여부
     */
    @Column(name = "pswd_reuse_yn", length = 1)
    private String pswdReuseYn;

    /**
     * 비밀번호 최소 길이
     */
    @Column(name = "pswd_min_len")
    private BigDecimal pswdMinLen;

    /**
     * 비밀번호 대문자 요구 여부
     */
    @Column(name = "pswd_uc_req_yn", length = 1)
    private String pswdUcReqYn;

    /**
     * 비밀번호 소문자 요구 여부
     */
    @Column(name = "pswd_lc_req_yn", length = 1)
    private String pswdLcReqYn;

    /**
     * 비밀번호 숫자 요구 여부
     */
    @Column(name = "pswd_num_req_yn", length = 1)
    private String pswdNumReqYn;

    /**
     * 비밀번호 특수 문자 요구 여부
     */
    @Column(name = "pswd_spc_char_req_yn", length = 1)
    private String pswdSpcCharReqYn;

    /**
     * 비밀번호 금지 문자
     */
    @Column(name = "pswd_deny_char", length = 500)
    private String pswdDenyChar;
} 