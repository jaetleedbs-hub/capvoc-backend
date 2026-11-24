package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 메뉴 저장 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 저장 요청 DTO")
public class MenuSaveReqDto {

    @Schema(description = "저장할 메뉴 목록", required = true)
    private List<MenuSaveItemDto> menus;
}
