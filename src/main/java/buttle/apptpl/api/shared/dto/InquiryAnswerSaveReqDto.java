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
 * 서비스 문의 답변 저장 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryAnswerSaveReqDto extends BaseDto {

    @Schema(description = "문의ID", type = "string")
    private String qstnId;

    @Schema(description = "답변 내용", type = "string")
    private String answerCn;

    @Schema(description = "수정자ID", type = "string")
    private String mdfrId;

    @Schema(description = "답변자명", type = "string")
    private String answerNm;    
}
