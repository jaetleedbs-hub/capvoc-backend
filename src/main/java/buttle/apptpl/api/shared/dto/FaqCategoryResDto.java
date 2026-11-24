package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * BO302_P1 FAQ 분류 목록 조회 응답 행
 * {@link buttle.apptpl.api.shared.vo.CategoryListResVo}와 필드명을 맞춰 ModelMapper 매핑 및 프론트 JSON과 일치시킨다.
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Schema(description = "FAQ 분류 목록 행")
public class FaqCategoryResDto extends BaseDto {

    @Schema(description = "코드")
    private String cd;

    @Schema(description = "코드명(분류명)")
    private String cdNm;

    @Schema(description = "코드그룹")
    private String cdGroup;

    @Schema(description = "정렬순서")
    private Integer sortOrder;

    @Schema(description = "사용여부")
    private String useYn;
}
