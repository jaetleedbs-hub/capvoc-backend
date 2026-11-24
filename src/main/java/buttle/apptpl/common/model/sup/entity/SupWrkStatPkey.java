package buttle.apptpl.common.model.sup.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SupWrkStatPkey implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "crtr_ymd", nullable = false, length = 8)
    private String crtrYmd;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;
}