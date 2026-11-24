package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 사용자 상세 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDetailResDto extends BaseDto {
    
    @Schema(description = "사용자ID")
    private String userId;
    
    @Schema(description = "사용자명")
    private String userNm;
    
    @Schema(description = "사용자등급코드")
    private String userGradeCd;
    
    @Schema(description = "사용자유형코드")
    private String userTypeCd;
    
    @Schema(description = "조직코드")
    private String ognzCd;
    
    @Schema(description = "조직명")
    private String ognzNm;
    
    @Schema(description = "입사일자")
    private String jncmpYmd;
    
    @Schema(description = "퇴사일자")
    private String rsgntnYmd;
    
    @Schema(description = "자택전화번호")
    private String homeTelNo;
    
    @Schema(description = "휴대전화번호")
    private String mblTelNo;
    
    @Schema(description = "이메일주소")
    private String emlAddr;
    
    @Schema(description = "사용여부")
    private String useYn;
    
    @Schema(description = "로그인여부")
    private String lgnYn;
    
    @Schema(description = "로그인IP")
    private String lgnIp;
    
    @Schema(description = "로그인시도횟수")
    private Integer lgnTryCnt;
    
    @Schema(description = "비밀번호변경일시")
    private String pswdChgDt;
    
    @Schema(description = "사용자사진")
    private String userPhoto;
}
