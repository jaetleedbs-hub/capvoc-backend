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
 * 즐겨찾기카테고리관리 복합키
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SupBokmkCtgyPkey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 즐겨찾기 카테고리 시퀀스ID
     */
    @Column(name = "bokmk_ctgy_sn")
    private Integer bokmkCtgySn;

    /**
     * 사용자 ID
     */
    @Column(name = "user_id", length = 50)
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupBokmkCtgyPkey that = (SupBokmkCtgyPkey) o;
        return Objects.equals(bokmkCtgySn, that.bokmkCtgySn) && 
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bokmkCtgySn, userId);
    }

} 