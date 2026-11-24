package buttle.apptpl.log.access.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 프로그램접속이력 요청 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6040001Vo {

    // private Integer logSid;
    private String userId;
    private String ipAddr;
    private String urlAddr;
    private String startDate;
    private String endDate;
} 