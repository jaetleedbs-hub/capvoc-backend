package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * FAQ 삭제 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "FAQ 삭제 요청")
public class FaqDeleteReqDto {
    
    @Schema(description = "게시판 ID")
    private String bbsId;
    
    @Schema(description = "게시물 ID (단일 삭제)")
    private String pstId;

}
