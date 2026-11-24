package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 상세 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "FAQ 상세 응답")
public class FaqDetailResDto {
    
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
    
    @Schema(description = "답변")
    private String answer;
    
    @Schema(description = "노출 여부")
    private String displayYn;
    
    @Schema(description = "정렬 순서")
    private Integer sortOrder;
    
    @Schema(description = "등록일")
    private String regDt;
    
    @Schema(description = "등록자 ID")
    private String rgtrId;
    
    @Schema(description = "수정일")
    private String updDt;
    
    @Schema(description = "수정자 ID")
    private String mdfrId;
}
