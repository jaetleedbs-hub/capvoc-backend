package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 서비스 문의 목록 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryListResVo {

    private String qstnId;
    private String cmpyId;
    private String inquiryTypeCd;
    private String inquiryTypeNm;
    private String inquiryCn;
    private String qstnCn;
    private String qstnTtl;
    private Boolean hasFile;
    private String atchFileGroupId;
    private String cmpyNm;
    private String mbrId;
    private String inquirerNm;
    private String inquirerId;
    private String rcptDt;
    private String answerYn;
    private String answerDt;
    private String answerCn;
    private String answererNm;
}
