package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 상세 팝업 응답")
public class NoticePopupDetailResDto {
    @Schema(description = "게시물ID")
    private String pstId;
    
    @Schema(description = "게시판ID")
    private String bbsId;
    
    @Schema(description = "제목")
    private String ttl;
    
    @Schema(description = "게시내용")
    private String pstCn;
    
    @Schema(description = "공지여부")
    private String ntcYn;
    
    @Schema(description = "노출여부")
    private String viewUseYn;
    
    @Schema(description = "첨부파일그룹ID")
    private String atchFileGroupId;
    
    @Schema(description = "조회수")
    private Integer inqCnt;
    
    @Schema(description = "등록일시")
    private String regDt;
    
    @Schema(description = "등록자ID")
    private String rgtrId;
}
