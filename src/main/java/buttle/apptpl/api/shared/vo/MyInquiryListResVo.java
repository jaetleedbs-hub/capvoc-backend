package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 내 서비스 문의 목록 응답 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MyInquiryListResVo {

    private Integer rn;
    private String cmpyId;
    private String srvcQstnId;
    private String srvcQstnClsfCd;
    private String srvcQstnClsfNm;
    private String srvcQstnCn;
    private String srvcAnsCn;
    private String srvcAnsDt;
    private String srvcRmrk;
    private String srvcQstnRcptYn;
    private String rgtrId;
    private String regDt;
    private String svcQstnNm;
    private String svcAnsNm;
    private String fileYn;
    private String atchFileGroupId;
    private String answerYn;
    private String fileNames;
}
