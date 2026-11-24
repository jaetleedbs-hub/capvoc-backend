package buttle.apptpl.common.model.cmpt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "cmpt_sale_day")
public class CmptSaleDayEntity extends BaseAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptSaleDayPkey id;

    @Column(name = "hldy_se_cd", length = 20)
    private String hldySeCd;

    @Column(name = "wday_se_cd", length = 20)
    private String wdaySeCd;

    @Column(name = "rmrk", length = 500)
    private String rmrk;
} 