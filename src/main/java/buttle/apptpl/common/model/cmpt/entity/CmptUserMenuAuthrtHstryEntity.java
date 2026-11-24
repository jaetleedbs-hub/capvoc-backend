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
 * 사용자메뉴권한이력
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usr_menu_auth_hist", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptUserMenuAuthrtHstryEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 권한 이력 시퀀스 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_hist_srno", nullable = false)
    private Integer authrtHstrySid;

    /**
     * 처리자 ID
     */
    @Column(name = "prcs_usr_id", length = 255)
    private String prcrId;

    /**
     * 처리 조직 코드
     */
    @Column(name = "prcs_ognz_cd", length = 50)
    private String prcsOgnzCd;

    /**
     * 처리 일시
     */
    @Column(name = "prcs_dttm")
    @Convert(converter = buttle.apptpl.common.model.converter.CompactDateTimeStringConverter.class)
    private String prcsDt;

    /**
     * 사용자 ID
     */
    @Column(name = "usr_id", length = 255)
    private String userId;

    /**
     * 메뉴 ID
     */
    @Column(name = "menu_id", length = 50)
    private String menuId;

    /**
     * 권한 부여 구분 코드
     */
    @Column(name = "auth_grnt_dvcd", length = 20)
    private String authrtGrntSeCd;
} 