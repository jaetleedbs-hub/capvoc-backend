package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 메뉴 저장 항목 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 저장 항목 DTO")
public class MenuSaveItemDto {

    @Schema(description = "회원사 유형 코드", example = "PARTNER", required = true)
    @NotBlank(message = "회원사 유형 코드는 필수입니다.")
    @Size(max = 20, message = "회원사 유형 코드는 20자 이내여야 합니다.")
    private String cmpyTypeCd;

    @Schema(description = "메뉴 ID", example = "MENU_001", required = true)
    @NotBlank(message = "메뉴 ID는 필수입니다.")
    @Size(max = 50, message = "메뉴 ID는 50자 이내여야 합니다.")
    private String menuId;

    @Schema(description = "메뉴명", example = "대시보드", required = true)
    @NotBlank(message = "메뉴명은 필수입니다.")
    @Size(max = 100, message = "메뉴명은 100자 이내여야 합니다.")
    private String menuNm;

    @Schema(description = "URL 주소", example = "/dashboard")
    @Size(max = 200, message = "URL 주소는 200자 이내여야 합니다.")
    private String urlAddr;

    @Schema(description = "새창 여부", example = "N")
    @Size(max = 1, message = "새창 여부는 1자여야 합니다.")
    private String npagYn;

    @Schema(description = "상위 메뉴 ID", example = "MENU_ROOT")
    @Size(max = 50, message = "상위 메뉴 ID는 50자 이내여야 합니다.")
    private String upMenuId;

    @Schema(description = "정렬 순번", example = "1")
    private Integer sortSn;

    @Schema(description = "화면 크기", example = "1024x768")
    @Size(max = 20, message = "화면 크기는 20자 이내여야 합니다.")
    private String scrnSize;

    @Schema(description = "메뉴 사용 여부", example = "Y", required = true)
    @NotBlank(message = "메뉴 사용 여부는 필수입니다.")
    @Size(max = 1, message = "메뉴 사용 여부는 1자여야 합니다.")
    private String menuUseYn;
}
