package buttle.apptpl.common.model.cmpt.entity;

import buttle.apptpl.common.data.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 참조 코드
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ref_cd", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptRfrncCdEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptRfrncCdPkey id;

    /**
     * 참조 명
     */
    @Column(name = "ref_cd_nm", length = 100)
    private String rfrncNm;

    /**
     * 참조 값
     */
    @Column(name = "ref_vlu", length = 100)
    private String rfrncVl;
} 