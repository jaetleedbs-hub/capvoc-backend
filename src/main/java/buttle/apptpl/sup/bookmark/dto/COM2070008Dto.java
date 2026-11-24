package buttle.apptpl.sup.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "즐겨찾기 목록 조회 응답")
public class COM2070008Dto {

    @Schema(description = "사용자 ID", example = "demo01")
    private String userId;

    @Schema(description = "카테고리 ID", example = "1")
    private Integer bokmkCtgySn;

    @Schema(description = "카테고리 이름", example = "즐겨찾기")
    private String bokmkCtgyNm;

    @Schema(description = "메뉴 ID", example = "SYS5010")
    private String bokmkMenuId;

    @Schema(description = "메뉴 이름", example = "사용자관리")
    private String menuNm;

    @Schema(description = "카테고리 정렬 순번", example = "1")
    private Integer ctgySortSn;

    @Schema(description = "메뉴 정렬 순번", example = "1")
    private Integer menuSortSn;
} 