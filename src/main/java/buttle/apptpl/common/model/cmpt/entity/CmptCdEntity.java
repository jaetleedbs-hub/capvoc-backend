package buttle.apptpl.common.model.cmpt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;
import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmmn_cd", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptCdEntity extends BaseAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptCdPkey id;

    @Column(name = "cmmn_cd_nm", length = 100)
    private String cdNm;

    @Column(name = "cd_dvcd", length = 20)
    private String cdSe;

    @Column(name = "use_yn", length = 1)
    private String useYn;

    @Column(name = "sort_seq")
    private Integer sortSn;
}