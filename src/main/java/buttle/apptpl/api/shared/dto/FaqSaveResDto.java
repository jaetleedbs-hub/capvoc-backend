package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 저장 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "FAQ 저장 응답")
public class FaqSaveResDto {
    
    @Schema(description = "게시판 ID")
    private String bbsId;
    
    @Schema(description = "게시물 ID")
    private String pstId;
}
