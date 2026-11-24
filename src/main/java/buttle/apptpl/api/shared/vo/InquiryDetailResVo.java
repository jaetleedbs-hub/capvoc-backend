package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 서비스 문의 상세 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryDetailResVo {

    private String qstnId;
    private String cmpyId;
    private String mbrId;
    private String cmpyNm;
    private String mbrNm;
    private String inquiryTypeCd;
    private String inquiryTypeNm;
    private String qstnTtl;
    private String qstnCn;
    private String rcptDt;
    private String atchFileGroupId;
    private String answererNm;
    private String answerYn;
    private String answerDt;
    private String answerCn;
}
