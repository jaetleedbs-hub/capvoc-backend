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
public class CmptPrgrmAuthrtPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "prgrm_id", nullable = false, length = 50)
    private String prgrmId;

    @Column(name = "auth_cd", nullable = false, length = 20)
    private String authrtCd;
} 