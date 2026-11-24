package buttle.apptpl.log.access.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 프로그램접속이력 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6040002Vo {

    private String logSid;
    private String logDt;
    private String userId;
    private String userNm;
    private String httpMethod;
    private String urlAddr;
    private String ipAddr;

} 