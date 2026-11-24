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
public class CmptSrvcQstnPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 회원사ID
     */
    @Column(name = "cmpy_id", nullable = false, length = 50)
    private String cmpyId;

    /**
     * 서비스문의ID
     */
    @Column(name = "srvc_qstn_id", nullable = false, length = 50)
    private String srvcQstnId;
}
