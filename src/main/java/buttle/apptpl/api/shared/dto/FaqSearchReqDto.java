package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 검색 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "FAQ 검색 요청")
public class FaqSearchReqDto {
    
    @Schema(description = "분류 코드")
    private String categoryCd;
    
    @Schema(description = "노출 여부")
    private String displayYn;
    
    @Schema(description = "등록일 시작 (YYYY-MM-DD)")
    private String startDate;
    
    @Schema(description = "등록일 종료 (YYYY-MM-DD)")
    private String endDate;
    
    @Schema(description = "검색어")
    private String searchKeyword;
}
