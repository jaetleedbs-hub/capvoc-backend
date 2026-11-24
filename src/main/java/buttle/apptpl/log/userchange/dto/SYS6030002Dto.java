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
 * 사용자변경이력 조회 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6030002Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "사용자 이름", type = "string")
    private String userNm;

    @Schema(description = "변경 일시", type = "string")
    private String chgDt;

    @Schema(description = "변경 유형", type = "string")
    private String chgTypeNm;

    @Schema(description = "변경 전 정보", type = "string")
    private String chgBfrInfoNm;

    @Schema(description = "변경 후 정보", type = "string")
    private String chgAftrInfoNm;

    @Schema(description = "처리자 이름", type = "string")
    private String prcrNm;
} 