package buttle.apptpl.common.model.sup.entity;

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
@Table(name = "sup_wrk_stat")
public class SupWrkStatEntity extends BaseAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private SupWrkStatPkey id;

    @Column(name = "ognz_cd", nullable = false, length = 50)
    private String ognzCd;
    
    @Column(name = "wrk_stat_cd", nullable = false, length = 20)
    private String wrkStatCd;

    @Column(name = "user_type_cd", length = 20)
    private String userTypeCd;
}