package buttle.apptpl.sup.memo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 슬라이드프레임_메모 카테고리 등록록
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM2080003Dto extends BaseDto {

    @Schema(description = "메모 카테고리 시퀀스ID", type = "integer")
    private Integer memoCtgySid;

    @Schema(description = "메모 카테고리 명", type = "string")
    private String memoCtgyNm;

    @Schema(description = "정렬일련번호", type = "integer")
    private Integer sortSn;
} 