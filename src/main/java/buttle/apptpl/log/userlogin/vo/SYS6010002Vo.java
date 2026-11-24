package buttle.apptpl.log.userlogin.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

/**
 * 사용자로그인이력 조회 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6010002Vo {

    private String userId;
    private String userNm;
    private String ognzNm;
    private String lgnYmd;
    private String lgnIp;
    private String lgnDt;
    private String lgtDt;
    private BigDecimal lgnCnt;
} 