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
 * 사용자 정보 저장 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserSaveReqDto extends BaseDto {
    
    @Schema(description = "사용자ID", required = true)
    private String userId;
    
    @Schema(description = "사용자명", required = true)
    private String userNm;
    
    @Schema(description = "사용자등급코드", required = true)
    private String userGradeCd;
    
    @Schema(description = "사용자유형코드", required = true)
    private String userTypeCd;
    
    @Schema(description = "조직코드", required = true)
    private String ognzCd;
    
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
    
    @Schema(description = "사용여부", required = true)
    private String useYn;
    
    @Schema(description = "사용자사진")
    private String userPhoto;
}
