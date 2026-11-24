package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 공통 응답")
public class NoticeCommonResDto {
    @Schema(description = "성공 건수")
    private Integer successCount;
    @Schema(description = "결과 메시지")
    private String message;
}
