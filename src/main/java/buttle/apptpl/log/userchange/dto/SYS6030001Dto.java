package buttle.apptpl.log.userchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 사용자변경이력 조회 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6030001Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;
    
    @Schema(description = "변경 유형 코드", type = "string")
    private String chgTypeCd;

    @Schema(description = "처리 일시", type = "string")
    private String prcsDt;

    @Schema(description = "조직 코드", type = "string")
    private String ognzCd;

    @Schema(description = "조회 시작일", type = "string")
    private String startDate;

    @Schema(description = "조회 종료일", type = "string")
    private String endDate;
} 