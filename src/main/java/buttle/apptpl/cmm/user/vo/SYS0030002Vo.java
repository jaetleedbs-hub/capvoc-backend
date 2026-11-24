package buttle.apptpl.cmm.user.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0030002Vo {

    private String userId;
    private String userNm;
    private String authrtCd;
    private String userTypeCd;
    private String ognzCd;
    private String ognzNm;
    private String lgnYn;
    private String lgnTryCnt;
    private String lgnIp;
    private String pswdChgDt;
    private String jncmpYmd;
    private String rsgntnYmd;
    private String homeTelNo;
    private String mblTelNo;
    private String emlAddr;
    private String userPhoto;
    private String useYn;
    private String ctiUseYn;
    private String ctiAutoLgnYn;
    private String ctiId;
    private String empNo;
    private String extNo;
    private String workTm;
    private String dscsnTaskSeCd;
    private String callbakTaskUseYn;
} 


