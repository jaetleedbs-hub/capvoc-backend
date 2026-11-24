package buttle.apptpl.api.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import buttle.apptpl.common.data.BaseDto;

/**
 * 문의 유형 조회 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryTypeResDto extends BaseDto {

    @Schema(description = "코드", type = "string")
    private String cd;

    @Schema(description = "코드명", type = "string")
    private String cdNm;
}
