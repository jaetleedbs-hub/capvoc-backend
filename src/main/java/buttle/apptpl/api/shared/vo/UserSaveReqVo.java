package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사용자 정보 저장 요청 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserSaveReqVo {
    
    private String userId;
    private String userNm;
    private String userGradeCd;
    private String userTypeCd;
    private String ognzCd;
    private String jncmpYmd;
    private String rsgntnYmd;
    private String homeTelNo;
    private String mblTelNo;
    private String emlAddr;
    private String useYn;
    private String userPhoto;
    private String rgtrId;
    private String mdfrId;
    private String pswd;
    
}
