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
 * 사용자 검색 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserSearchReqDto extends BaseDto {
    
    @Schema(description = "조직 코드", example = "ORG001")
    private String ognzCd;
    
    @Schema(description = "사용자 등급 코드", example = "ADMIN")
    private String userGradeCd;
    
    @Schema(description = "사용자 ID", example = "user001")
    private String userId;
    
    @Schema(description = "사용자명", example = "홍길동")
    private String userNm;
    
    @Schema(description = "사용 여부", example = "Y")
    private String useYn;
}
