package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 비밀번호이력 PKEY
 */
@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CmptPswdHstryPkey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "chg_dt", nullable = false)
    private LocalDateTime chgDt;
} 