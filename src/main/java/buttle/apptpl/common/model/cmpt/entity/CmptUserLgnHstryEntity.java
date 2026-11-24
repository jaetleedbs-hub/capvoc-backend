package buttle.apptpl.common.model.cmpt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_user_lgn_hstry")
public class CmptUserLgnHstryEntity extends BaseAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptUserLgnHstryPkey id;

    @Column(name = "lgn_ip", length = 15)
    private String lgnIp;

    @Column(name = "last_cntn_idnty_dt")
    private Timestamp lastCntnIdntyDt;

    @Column(name = "lgn_dt")
    private Timestamp lgnDt;

    @Column(name = "lgt_dt")
    private Timestamp lgtDt;

    @Column(name = "lgn_cnt")
    private BigDecimal lgnCnt;
}