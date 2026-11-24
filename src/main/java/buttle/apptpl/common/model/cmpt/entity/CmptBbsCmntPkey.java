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
public class CmptBbsCmntPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 게시물ID
     */
    @Column(name = "pst_id", nullable = false, length = 50)
    private String pstId;

    /**
     * 댓글ID
     */
    @Column(name = "cmnt_id", nullable = false, length = 50)
    private String cmntId;
}

