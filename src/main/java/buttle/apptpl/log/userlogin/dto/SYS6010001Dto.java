package buttle.apptpl.log.userlogin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 사용자로그인이력 조회 요청
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6010001Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;
    
    @Schema(description = "조직 코드", type = "string")
    private String ognzCd;

    @Schema(description = "조회 로그인 시작일", type = "string")
    private String startDate;

    @Schema(description = "조회 로그인 종료일", type = "string")
    private String endDate;

} 