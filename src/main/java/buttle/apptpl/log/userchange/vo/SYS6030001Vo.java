package buttle.apptpl.log.userchange.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사용자변경이력 조회 요청 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6030001Vo {

    private String userId;
    private String chgTypeCd;
    private String ognzCd;
    private String startDate;
    private String endDate;
} 