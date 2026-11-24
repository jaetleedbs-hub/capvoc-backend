package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 상세 응답")
public class NoticeDetailResDto {
    @Schema(description = "게시물ID")
    private String pstId;
    @Schema(description = "게시판ID")
    private String bbsId;
    @Schema(description = "제목")
    private String ttl;
    @Schema(description = "게시물내용")
    private String pstCn;
    @Schema(description = "공지여부")
    private String ntcYn;
    @Schema(description = "상단고정여부")
    private String topStdYn;
    @Schema(description = "노출여부")
    private String viewUseYn;
    @Schema(description = "게시시작일자", example = "20260101")
    private String bbsStYmd;
    @Schema(description = "게시시작시간", example = "00:00")
    private String bbsStTm;
    @Schema(description = "게시종료일자", example = "20261231")
    private String bbsEdYmd;
    @Schema(description = "게시종료시간", example = "23:59")
    private String bbsEdTm;
    @Schema(description = "게시시작일시", example = "2026-01-01 00:00")
    private String bbsStDt;
    @Schema(description = "게시종료일시", example = "2026-12-31 23:59")
    private String bbsEdDt;
    @Schema(description = "첨부파일그룹ID")
    private String atchFileGroupId;
    @Schema(description = "조회수")
    private Integer inqCnt;
    @Schema(description = "등록자ID")
    private String regId;
    @Schema(description = "등록자명")
    private String regNm;
    @Schema(description = "등록일시")
    private String regDt;
}
