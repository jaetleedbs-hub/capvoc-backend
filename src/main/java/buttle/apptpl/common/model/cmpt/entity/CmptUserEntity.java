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
 * 사용자
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usr", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptUserEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 사용자 ID
     */
    @Id
    @Column(name = "usr_id", nullable = false, length = 255)
    private String userId;

    /**
     * 사용자 명
     */
    @Column(name = "usr_nm", length = 50)
    private String userNm;

    /**
     * 비밀번호
     */
    @Column(name = "pswd", length = 255)
    private String pswd;

    /**
     * 조직 코드
     */
    @Column(name = "ognz_cd", length = 50)
    private String ognzCd;

    /**
     * 권한 코드
     */
    @Column(name = "auth_cd", length = 20)
    private String authrtCd;

    /**
     * 로그인 여부
     */
    @Column(name = "lgn_yn", length = 1)
    private String lgnYn;

    /**
     * 로그인 시도 수
     */
    @Column(name = "lgn_try_rcn")
    private Integer lgnTryCnt;

    /**
     * 로그인 IP
     */
    @Column(name = "lgn_ip", length = 15)
    private String lgnIp;

    /**
     * 비밀번호 변경일시
     */
    @Column(name = "pswd_chg_dttm")
    @Convert(converter = buttle.apptpl.common.model.converter.CompactDateTimeStringConverter.class)
    private String pswdChgDt;

    /**
     * 사용자 유형 코드
     */
    @Column(name = "usr_tpcd", length = 20)
    private String userTypeCd;

    /**
     * 입사 일자
     */
    @Column(name = "entr_dt")
    @Convert(converter = buttle.apptpl.common.model.converter.CompactDateStringConverter.class)
    private String jncmpYmd;

    /**
     * 퇴사 일자
     */
    @Column(name = "rtir_dt")
    @Convert(converter = buttle.apptpl.common.model.converter.CompactDateStringConverter.class)
    private String rsgntnYmd;

    /**
     * 자택 전화 번호
     */
    @Column(name = "home_tel_no", length = 11)
    private String homeTelNo;

    /**
     * 휴대 전화 번호
     */
    @Column(name = "mbl_tel_no", length = 11)
    private String mblTelNo;

    /**
     * 이메일 주소
     */
    @Column(name = "eml_addr", length = 40)
    private String emlAddr;

    /**
     * 사용자 사진
     */
    @Column(name = "usr_phto", columnDefinition = "TEXT")
    private String userPhoto;

    /**
     * 사용 여부
     */
    @Column(name = "use_yn", length = 1)
    private String useYn;

    /**
     * CTI 사용여부
     */
    @Column(name = "cti_use_yn", length = 1)
    private String ctiUseYn;

    /**
     * CTI 자동 로그인 여부
     */
    @Column(name = "cti_auto_lgn_yn", length = 1)
    private String ctiAutoLgnYn;

    /**
     * CTI ID
     */
    @Column(name = "cti_agnt_id", length = 50)
    private String ctiId;

    /**
     * 내선 번호
     */
    @Column(name = "ext_no", length = 8)
    private String extNo;

    /**
     * 상담 업무 구분 코드
     */
    @Column(name = "cnsl_task_dvcd", length = 20)
    private String dscsnTaskSeCd;

    /**
     * 콜백 업무 사용 여부
     */
    @Column(name = "clbk_task_use_yn", length = 1)
    private String callbakTaskUseYn;

    /**
     * 직원 번호
     */
    @Column(name = "emp_no", length = 32)
    private String empNo;

    /**
     * 인사 조직 코드
     */
    @Column(name = "hr_ognz_cd", length = 50)
    private String hrOgnzCd;

    /**
     * 근무 시간
     */
    @Column(name = "work_hm", length = 4)
    private String workTm;

    /**
     * 조직 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ognz_cd", referencedColumnName = "ognz_cd", insertable = false, updatable = false)
    private CmptOgnzEntity cmptOgnz;
}