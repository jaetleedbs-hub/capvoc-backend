package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 서비스 문의 검색 조건 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquirySearchReqVo {

    private String fromDt;
    private String toDt;
    private String answerYn;
    private String inquiryTypeCd;
    private String cmpyNm;
    private String keyword;
    private Long lastQstnId;
}
