package buttle.apptpl.common.data;

import java.util.UUID;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass // 여러 엔티티에 공통 필드를 상속하기 위함
@Getter
@Setter
public class BaseDto {
    /**
     * 고유 레코드 id
     */
    private String recid = UUID.randomUUID().toString();
}
