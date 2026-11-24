package buttle.apptpl.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 기본 VO 클래스 (MyBatis용)
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseVo {

    /**
     * 등록자 ID
     */
    private String rgtrId;

    /**
     * 등록 일시
     */
    private LocalDateTime regDt;

    /**
     * 수정자 ID
     */
    private String mdfrId;

    /**
     * 수정 일시
     */
    private LocalDateTime mdfcnDt;

}
