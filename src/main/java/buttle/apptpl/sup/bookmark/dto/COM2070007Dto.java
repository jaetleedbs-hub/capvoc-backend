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
 * 즐겨찾기 카테고리 삭제 응답
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM2070007Dto extends BaseDto {

    @Schema(description = "즐겨찾기 카테고리 시퀀스ID", type = "integer")
    private Integer bokmkCtgySn;

    @Schema(description = "메시지", type = "string")
    private String message;
} 