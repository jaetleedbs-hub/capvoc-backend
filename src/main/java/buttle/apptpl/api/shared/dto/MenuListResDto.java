package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 메뉴 조회 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 조회 응답 DTO")
public class MenuListResDto {

    @Schema(description = "회원사 유형 코드", example = "PARTNER")
    private String cmpyTypeCd;

    @Schema(description = "메뉴 ID", example = "MENU_001")
    private String menuId;

    @Schema(description = "메뉴명", example = "대시보드")
    private String menuNm;

    @Schema(description = "URL 주소", example = "/dashboard")
    private String urlAddr;

    @Schema(description = "새창 여부", example = "N")
    private String npagYn;

    @Schema(description = "상위 메뉴 ID", example = "MENU_ROOT")
    private String upMenuId;

    @Schema(description = "정렬 순번", example = "1")
    private Integer sortSn;

    @Schema(description = "화면 크기", example = "1024x768")
    private String scrnSize;

    @Schema(description = "메뉴 사용 여부", example = "Y")
    private String menuUseYn;
}
