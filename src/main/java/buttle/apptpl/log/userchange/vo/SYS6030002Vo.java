package buttle.apptpl.log.userchange.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사용자변경이력 조회 응답 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6030002Vo {

    private String userId;
    private String userNm;
    private String chgDt;
    private String chgTypeNm;
    private String chgBfrInfoNm;
    private String chgAftrInfoNm;
    private String prcrNm;
} 