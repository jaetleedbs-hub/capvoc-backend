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
public class CmptBbsMngrPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 게시판ID
     */
    @Column(name = "bbs_id", nullable = false, length = 50)
    private String bbsId;

    /**
     * 관리자ID
     */
    @Column(name = "mngr_id", nullable = false, length = 50)
    private String mngrId;
}

