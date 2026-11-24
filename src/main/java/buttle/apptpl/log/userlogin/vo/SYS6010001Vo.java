package buttle.apptpl.log.userlogin.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 사용자로그인이력 조회 요청 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6010001Vo {

    private String userId;
    private String lgnYmd;
    private String startDate;
    private String endDate;
    private String ognzCd;
    private String ognzNm;
    private String userNm;
    private String keyword;
} 