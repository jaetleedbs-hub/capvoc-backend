package buttle.apptpl.cmm.user.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0030001Vo {

    private String userId;
    private String userNm;
    private String ognzCd;
    private String ognzNm;
    private String authrtCd;
    private String authrtNm;
    private String ctiUseYn;
    private String useYn;
    private String lgnYn;
    private String lgnIp;
    private String jncmpYmd;
    private String rsgntnYmd;
    private String wrkStatNm;
    private String workTmCd;
    private String workTm;
    private String mdfrId;
} 