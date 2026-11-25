package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import buttle.apptpl.common.data.BaseAuditingEntity;

@Builder
@Entity
@Table(name = "cmpt_ip", schema = "cdm_own")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CmptIpEntity extends BaseAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "lgn_ip", nullable = false, length = 15)
    private String lgnIp;

    @Column(name = "ext_no", length = 20)
    private String extNo;
} 