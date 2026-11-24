package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 분류 저장 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "FAQ 분류 저장 요청")
public class CategorySaveReqDto {
    
    @Schema(description = "코드 (수정 시)")
    private String cd;
    
    @Schema(description = "코드명")
    private String cdNm;
    
    @Schema(description = "정렬순서")
    private Integer sortOrder;
}
