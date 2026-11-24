package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 메뉴 조회 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 조회 요청 DTO")
public class MenuListReqDto {

    @Schema(description = "회원사 유형 코드", example = "PARTNER", required = true)
    private String cmpyTypeCd;
}
