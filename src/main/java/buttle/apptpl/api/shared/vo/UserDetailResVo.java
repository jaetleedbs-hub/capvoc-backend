package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사용자 상세 응답 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDetailResVo {
    
    private String userId;
    private String userNm;
    private String userGradeCd;
    private String userTypeCd;
    private String ognzCd;
    private String ognzNm;         // 조직명
    private String jncmpYmd;
    private String rsgntnYmd;
    private String homeTelNo;
    private String mblTelNo;
    private String emlAddr;
    private String useYn;
    private String lgnYn;
    private String lgnIp;
    private Integer lgnTryCnt;
    private String pswdChgDt;
    private String userPhoto;
}
