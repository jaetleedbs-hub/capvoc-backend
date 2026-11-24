package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 상세 팝업 요청")
public class NoticePopupDetailReqDto {
    @Schema(description = "게시물ID")
    private String pstId;
}
