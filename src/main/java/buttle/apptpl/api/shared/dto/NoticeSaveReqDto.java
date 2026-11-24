package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 저장 요청")
public class NoticeSaveReqDto {
    @Schema(description = "게시물ID")
    private String pstId;
    @Schema(description = "게시판ID", required = true)
    private String bbsId;
    @Schema(description = "제목", required = true)
    private String ttl;
    @Schema(description = "게시물내용", required = true)
    private String pstCn;
    @Schema(description = "공지여부")
    private String ntcYn;
    @Schema(description = "상단고정여부")
    private String topStdYn;
    @Schema(description = "노출여부")
    private String viewUseYn;
    @Schema(description = "게시시작일자", example = "20260101", required = true)
    private String bbsStYmd;
    @Schema(description = "게시시작시간", example = "00:00")
    private String bbsStTm;
    @Schema(description = "게시종료일자", example = "20261231", required = true)
    private String bbsEdYmd;
    @Schema(description = "게시종료시간", example = "23:59")
    private String bbsEdTm;
    @Schema(description = "첨부파일그룹ID")
    private String atchFileGroupId;
    @Schema(description = "GDS 사용자 ID")
    private String gdsUserId;
}
