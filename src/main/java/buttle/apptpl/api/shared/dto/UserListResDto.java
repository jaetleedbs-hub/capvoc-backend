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
 * 사용자 목록 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserListResDto extends BaseDto {
    
    @Schema(description = "순번")
    private Integer rownum;
    
    @Schema(description = "사용자ID")
    private String userId;
    
    @Schema(description = "사용자명")
    private String userNm;
    
    @Schema(description = "소속부서코드")
    private String deptCd;
    
    @Schema(description = "소속부서명")
    private String deptNm;
    
    @Schema(description = "사용자등급코드")
    private String userGradeCd;
    
    @Schema(description = "사용자등급명")
    private String userGradeNm;
    
    @Schema(description = "로그인여부")
    private String lgnYn;
    
    @Schema(description = "로그인IP")
    private String lgnIp;
    
    @Schema(description = "근태코드")
    private String wrkStatCd;
    
    @Schema(description = "근태상태명")
    private String wrkStatNm;
    
    @Schema(description = "입사일자")
    private String jncmpYmd;
    
    @Schema(description = "퇴사일자")
    private String rsgntnYmd;
    
    @Schema(description = "사용시작일")
    private String useStartYmd;
    
    @Schema(description = "사용종료일")
    private String useEndYmd;
}
