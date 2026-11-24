package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 분류 목록 조회 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "FAQ 분류 목록 조회 요청")
public class CategoryListReqDto {
    
    @Schema(description = "분류명 검색어")
    private String searchKeyword;
}

