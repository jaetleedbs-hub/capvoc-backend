package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 내 서비스 문의 목록 검색 조건 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MyInquirySearchReqVo {

    private String companyId;
    private String answerYn;
    private String loginId;
}
