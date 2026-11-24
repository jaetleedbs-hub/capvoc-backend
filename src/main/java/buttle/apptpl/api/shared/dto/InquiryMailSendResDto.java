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
 * 서비스 문의 메일 발송 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryMailSendResDto extends BaseDto {

    @Schema(description = "메일 발송 성공 여부", type = "boolean")
    private Boolean success;

    @Schema(description = "결과 메시지", type = "string")
    private String message;
}
