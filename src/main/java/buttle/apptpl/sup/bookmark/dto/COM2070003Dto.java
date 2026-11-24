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
 * 즐겨찾기 카테고리 목록 조회 응답
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM2070003Dto extends BaseDto {

    @Schema(description = "즐겨찾기 카테고리 시퀀스ID", type = "integer")
    private Integer bokmkCtgySn;

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "즐겨찾기 카테고리명", type = "string")
    private String bokmkCtgyNm;

    @Schema(description = "정렬 순서", type = "integer")
    private Integer sortSn;

    @Schema(description = "등록일시", type = "string")
    private String rgstDttm;

    @Schema(description = "수정일시", type = "string")
    private String updtDttm;
} 