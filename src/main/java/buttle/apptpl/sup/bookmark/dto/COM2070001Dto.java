package buttle.apptpl.sup.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 슬라이드프레임_즐겨찾기 등록 요청
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM2070001Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "즐겨찾기 카테고리 시퀀스ID", type = "integer")
    private Integer bokmkCtgySn;

    @Schema(description = "즐겨찾기 메뉴 ID", type = "string")
    private String bokmkMenuId;

    @Schema(description = "액션", type = "string")
    private String action;

} 