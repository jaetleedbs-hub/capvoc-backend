package buttle.apptpl.common.model.sup.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 메모카테고리관리 복합키
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SupMemoCtgyPkey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 메모 카테고리 시퀀스ID
     */
    @Column(name = "memo_ctgy_sid")
    private Integer memoCtgySid;

    /**
     * 사용자 ID
     */
    @Column(name = "user_id", length = 50)
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupMemoCtgyPkey that = (SupMemoCtgyPkey) o;
        return Objects.equals(memoCtgySid, that.memoCtgySid) && 
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memoCtgySid, userId);
    }

} 