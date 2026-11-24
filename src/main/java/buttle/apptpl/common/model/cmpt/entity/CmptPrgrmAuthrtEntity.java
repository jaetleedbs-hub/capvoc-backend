package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;
import jakarta.persistence.EmbeddedId;

/**
 * 프로그램권한
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prgrm_auth", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptPrgrmAuthrtEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptPrgrmAuthrtPkey id;

    /**
     * 프로그램 엔티티와의 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "prgrm_id", referencedColumnName = "prgrm_id", insertable = false, updatable = false)
    })
    private CmptPrgrmEntity cmptPrgrm;
} 