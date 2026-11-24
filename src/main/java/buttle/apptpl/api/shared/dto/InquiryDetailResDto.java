package buttle.apptpl.api.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import buttle.apptpl.common.data.BaseDto;

/**
 * 서비스 문의 상세 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryDetailResDto extends BaseDto {

    @Schema(description = "문의ID", type = "string")
    private String qstnId;

    @Schema(description = "회원사ID", type = "string")
    private String cmpyId;

    @Schema(description = "회원ID", type = "string")
    private String mbrId;

    @Schema(description = "회원사명", type = "string")
    private String cmpyNm;

    @Schema(description = "회원명", type = "string")
    private String mbrNm;

    @Schema(description = "문의 유형 코드", type = "string")
    private String inquiryTypeCd;

    @Schema(description = "문의 유형명", type = "string")
    private String inquiryTypeNm;

    @Schema(description = "문의 제목 (100자)", type = "string")
    private String qstnTtl;

    @Schema(description = "문의 내용 (500자)", type = "string")
    private String qstnCn;

    @Schema(description = "접수일시", type = "string")
    private String rcptDt;

    @Schema(description = "첨부파일 그룹ID", type = "string")
    private String atchFileGroupId;

    @Schema(description = "답변자명", type = "string")
    private String answererNm;

    @Schema(description = "답변 상태", type = "string")
    private String answerYn;

    @Schema(description = "답변일시", type = "string")
    private String answerDt;

    @Schema(description = "답변 내용", type = "string")
    private String answerCn;
}
