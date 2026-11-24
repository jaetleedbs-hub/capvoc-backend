package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "파일 업로드 응답")
public class NoticeFileUploadResDto {
    @Schema(description = "첨부파일그룹ID")
    private String atchFileGroupId;
    @Schema(description = "파일명")
    private String fileName;
    @Schema(description = "파일크기")
    private Long fileSize;
}
