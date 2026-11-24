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
public class CmptUmsRcvrPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * UMSID
     */
    @Column(name = "ums_id", nullable = false, length = 50)
    private String umsId;

    /**
     * UMS_일련번호
     */
    @Column(name = "ums_sn", nullable = false)
    private Integer umsSn;
}
