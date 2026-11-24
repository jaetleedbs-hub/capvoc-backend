package buttle.apptpl.cmm.user.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

/**
 * 사용자목록 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0040003Vo {

    private String userId;
    private String userNm;
    private String pswd;
    private String ognzCd;
    private String ognzPathNm;
    private String authrtCd;
    private String authrtCdNm;
    private String lgnYn;
    private BigDecimal lgnTryCnt;
    private String lgnIp;
    private String pswdChgDt;
    private String userTypeCd;
    private String userTypeCdNm;
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
    private String dscsnTaskSeCd;
    private String dscsnTaskSeCdNm;
} 