package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 서비스 문의 등록 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "서비스 문의 등록 응답")
public class InquiryRegisterResDto {

    @Schema(description = "생성된 서비스 문의 ID")
    private String qstnId;

    @Schema(description = "처리 성공 여부")
    private boolean success;
}
