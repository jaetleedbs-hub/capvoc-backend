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
 * 사용자메뉴권한
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usr_menu_auth", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptUserMenuAuthrtEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptUserMenuAuthrtPkey id;

    /**
     * 권한 부여 구분 코드
     */
    @Column(name = "auth_grnt_dvcd", length = 20)
    private String authrtGrntSeCd;

    /**
     * 사용자 엔티티와의 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id", referencedColumnName = "usr_id", insertable = false, updatable = false)
    private CmptUserEntity cmptUser;
}