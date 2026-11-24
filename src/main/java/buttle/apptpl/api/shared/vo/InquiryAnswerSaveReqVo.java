package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 서비스 문의 답변 저장 요청 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryAnswerSaveReqVo {

    private String qstnId;
    private String answerCn;
    private String mdfrId;
    private String answerNm;
}
