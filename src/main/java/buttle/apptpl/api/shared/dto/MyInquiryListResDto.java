package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 내 서비스 문의 목록 응답 DTO
 *
 * <p>필드명은 상담사 콘솔 화면(InquiryHistoryItem)이 그대로 사용하는 계약이므로 임의 개명 금지.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "내 서비스 문의 목록 응답")
public class MyInquiryListResDto {

    @Schema(description = "순번(전체 기준 역순)")
    private Integer rn;

    @Schema(description = "회원사 ID")
    private String cmpyId;

    @Schema(description = "서비스 문의 ID")
    private String srvcQstnId;

    @Schema(description = "문의 분류 코드")
    private String srvcQstnClsfCd;

    @Schema(description = "문의 분류명")
    private String srvcQstnClsfNm;

    @Schema(description = "문의 내용")
    private String srvcQstnCn;

    @Schema(description = "답변 내용")
    private String srvcAnsCn;

    @Schema(description = "답변 일시")
    private String srvcAnsDt;

    @Schema(description = "비고")
    private String srvcRmrk;

    @Schema(description = "문의 접수 여부")
    private String srvcQstnRcptYn;

    @Schema(description = "등록자 ID")
    private String rgtrId;

    @Schema(description = "등록 일시")
    private String regDt;

    @Schema(description = "문의자명")
    private String svcQstnNm;

    @Schema(description = "답변자명")
    private String svcAnsNm;

    @Schema(description = "첨부 여부")
    private String fileYn;

    @Schema(description = "첨부파일 그룹 ID")
    private String atchFileGroupId;

    @Schema(description = "답변 여부 (Y/N)")
    private String answerYn;

    @Schema(description = "첨부파일명 목록 (/ 구분)")
    private String fileNames;
}
