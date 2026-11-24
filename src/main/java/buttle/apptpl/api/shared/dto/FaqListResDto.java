package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 목록 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "FAQ 목록 응답")
public class FaqListResDto {
    
    @Schema(description = "게시판 ID")
    private String bbsId;
    
    @Schema(description = "게시물 ID")
    private String pstId;
    
    @Schema(description = "분류 코드")
    private String categoryCd;
    
    @Schema(description = "분류명")
    private String categoryNm;
    
    @Schema(description = "질문")
    private String question;
    
    @Schema(description = "노출 여부")
    private String displayYn;

    @Schema(description = "조회수")
    private String viewCount;
    
    @Schema(description = "노출 여부명")
    private String displayYnNm;
    
    @Schema(description = "등록일")
    private String regDt;
    
    @Schema(description = "등록자 ID")
    private String rgtrId;
    
    @Schema(description = "등록자명")
    private String rgtrIdNm;
}
