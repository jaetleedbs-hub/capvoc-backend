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
 * SYS6020 오류로그 목록 결과 VO
 */
public class SYS6020002Vo {

    private Integer logSid;
    private String logDt;
    private String svcCd;
    private String sevCd;
    private String failKindCd;
    private String cmpyId;
    private String svrId;
    private String subject;
    private String errCd;

}
