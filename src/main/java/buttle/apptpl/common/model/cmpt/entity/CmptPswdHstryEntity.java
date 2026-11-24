package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import buttle.apptpl.common.data.BaseAuditingEntity;

/**
 * 비밀번호이력
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_pswd_hstry")
public class CmptPswdHstryEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptPswdHstryPkey id;

    /**
     * 비밀번호
     */
    @Column(name = "pswd", length = 255)
    private String pswd;
}