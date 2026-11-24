package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 서비스 문의 등록 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryRegisterReqVo {

    /** 서비스 문의 ID (서비스에서 채번) */
    private String srvcQstnId;

    private String companyId;
    private String srvcQstnClsfCd;
    private String srvcQstnCn;
    private String rgtrId;
    private String mdfrId;
    private String svcQstnNm;
    private String atchFileGroupId;
}
