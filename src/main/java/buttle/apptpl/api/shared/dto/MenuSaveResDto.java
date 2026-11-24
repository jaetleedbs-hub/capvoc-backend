package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 메뉴 저장 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 저장 응답 DTO")
public class MenuSaveResDto {

    @Schema(description = "처리 결과", example = "success")
    private String result;

    @Schema(description = "결과 메시지", example = "저장되었습니다.")
    private String message;
}
