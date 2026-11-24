package buttle.apptpl.cmm.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeDto {

    // 게시판 ID
    @Schema(description = "게시판 ID")
    private String bbsId;
    
    // 게시물 ID
    @Schema(description = "게시물 ID")
    private String pstId;
    
    // 제목
    @Schema(description = "제목")
    private String ttl;

    // 등록일시
    @Schema(description = "등록일시")
    private String rgtDt;

    // 읽음 여부
    @Schema(description = "읽음 여부")
    private String isRead;

    // 조회 여부
    @Schema(description = "조회 여부")
    private String isView;

    // 회사 ID
    @Schema(description = "회사 ID")
    private String cmpyId;

    // 회원 ID
    @Schema(description = "회원 ID")
    private String mbrId;

    // 미확인 여부
    @Schema(description = "미확인 여부")
    private String unreadOnly;

    // 화면 표시 여부
    @Schema(description = "화면 표시 여부")
    private String viewUseYn;
}
