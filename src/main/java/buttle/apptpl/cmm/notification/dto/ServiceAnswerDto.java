package buttle.apptpl.cmm.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceAnswerDto {
    // 문의 ID
    @Schema(description = "문의 ID")
    private String srvcQstnId;

    // 문의 내용
    @Schema(description = "문의 내용")
    private String srvcQstnCn;

    // 답변 내용
    @Schema(description = "답변 내용")
    private String srvcAnsCn;

    // 답변 등록일시
    @Schema(description = "답변 등록일시")
    private String srvcAnsRgtDt;

    // 화면 표시 여부
    @Schema(description = "화면 표시 여부")
    private String viewUseYn;

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
    private String isView;

    // 화면 표시 여부
    @Schema(description = "읽음 여부")
    private String isRead;

}
