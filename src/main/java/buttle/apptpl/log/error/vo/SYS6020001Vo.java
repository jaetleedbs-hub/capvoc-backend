package buttle.apptpl.log.error.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
/**
 * SYS6020 오류로그 조회 조건 VO
 */
public class SYS6020001Vo {

    private Integer logSid;
    private String svcCd;
    private String sevCd;
    private String failKindCd;
    private String cmpyId;
    private String subject;
    private String startDate;
    private String endDate;

}
