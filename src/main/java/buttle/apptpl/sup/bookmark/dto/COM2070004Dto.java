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
 * 즐겨찾기 카테고리 저장 요청
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM2070004Dto extends BaseDto {

    @Schema(description = "즐겨찾기 카테고리 시퀀스ID", type = "integer")
    private Integer bokmkCtgySn;

    @Schema(description = "즐겨찾기 카테고리명", type = "string")
    private String bokmkCtgyNm;

    @Schema(description = "정렬 순서", type = "integer")
    private Integer sortSn;
} 