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
public class CmptQnaAnswrPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 질의응답ID
     */
    @Column(name = "qna_id", nullable = false, length = 50)
    private String qnaId;

    /**
     * 답변 사용자ID
     */
    @Column(name = "ans_user_id", nullable = false, length = 50)
    private String ansUserId;
}
