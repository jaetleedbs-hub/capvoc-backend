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
 * 슬라이드프레임_즐겨찾기 등록 응답
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM2070002Dto extends BaseDto {
    @Schema(description = "액션", type = "string")
    private String action;

    @Schema(description = "메시지", type = "string")
    private String message;
} 