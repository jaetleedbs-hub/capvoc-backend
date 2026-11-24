package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 검색 요청")
public class NoticeSearchReqDto {
    @Schema(description = "게시판ID")
    private String bbsId;
    @Schema(description = "제목")
    private String ttl;
    @Schema(description = "공지여부")
    private String ntcYn;
    @Schema(description = "노출여부")
    private String viewUseYn;
    @Schema(description = "게시시작일자(검색용)", example = "20260101")
    private String bbsStYmd;
    @Schema(description = "게시종료일자(검색용)", example = "20261231")
    private String bbsEdYmd;
}
