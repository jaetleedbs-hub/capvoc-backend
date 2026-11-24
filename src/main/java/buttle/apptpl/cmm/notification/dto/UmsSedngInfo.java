package buttle.apptpl.cmm.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UmsSedngInfo {

    // UMS 아이디
    @Schema(description = "UMS 아이디")
    private String umsId;

    // UMS 제목
    @Schema(description = "UMS 제목")
    private String umsTtl;

    // UMS 내용
    @Schema(description = "UMS 내용")
    private String umsCn;

    // 발송일시
    @Schema(description = "발송일시")
    private String sendDt;

    // 발송 결과 코드
    @Schema(description = "발송 결과 코드")
    private String sendRsltCd;

    // 미확인 여부
    @Schema(description = "미확인 여부")
    private String unreadOnly;

    // 화면 표시 여부
    @Schema(description = "화면 표시 여부")
    private String viewUseYn;

    // UMS 순번
    @Schema(description = "UMS 순번")
    private Integer umsSn;

    // 회사 ID
    @Schema(description = "회사 ID")
    private String cmpyId;

    // 회원 ID
    @Schema(description = "회원 ID")
    private String mbrId;

    // 화면 표시 여부
    @Schema(description = "화면 표시 여부")
    private String isView;

    // 화면 표시 여부
    @Schema(description = "읽음 여부")
    private String isRead;

}
