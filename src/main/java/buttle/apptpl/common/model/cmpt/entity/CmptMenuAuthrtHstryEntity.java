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
import java.time.LocalDateTime;

/**
 * 메뉴권한이력
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu_auth_hist", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptMenuAuthrtHstryEntity extends BaseAuditingEntity implements Serializable {

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
    private LocalDateTime prcsDt;

    /**
     * 권한 코드
     */
    @Column(name = "auth_cd", nullable = false, length = 20)
    private String authrtCd;

    /**
     * 메뉴 ID
     */
    @Column(name = "menu_id", nullable = false, length = 50)
    private String menuId;
} 