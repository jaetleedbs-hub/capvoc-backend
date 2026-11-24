package buttle.apptpl.common.model.cmpt.entity;

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
public class CmptSchdlMngPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 사용자ID
     */
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    /**
     * 일정ID
     */
    @Column(name = "schdl_id", nullable = false, length = 50)
    private String schdlId;
}
