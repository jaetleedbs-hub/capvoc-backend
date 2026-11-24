package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;

/**
 * 시스템기준정보관리
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_crtr_info", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptSysCrtrEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 시스템 기준 코드
     */
    @Id
    @Column(name = "sys_crtr_cd", length = 50)
    private String sysCrtrCd;

    /**
     * 시스템 기준 코드명
     */
    @Column(name = "sys_crtr_cd_nm", length = 100)
    private String sysCrtrCdNm;

    /**
     * 시스템 기준 코드 값
     */
    @Column(name = "sys_crtr_cd_vlu", length = 100)
    private String sysCrtrCdVl;

    /**
     * 시스템 기준 유형 코드
     */
    @Column(name = "sys_crtr_tpcd", length = 20)
    private String sysCrtrTypeCd;

    /**
     * 비고 내용
     */
    @Column(name = "sys_crtr_rmrk", length = 1000)
    private String rmrkCn;

    /**
     * 사용 여부
     */
    @Column(name = "use_yn", length = 1)
    private String useYn;
}
