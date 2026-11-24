package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 문의 유형 조회 요청 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryTypeSearchReqVo {

    /**
     * 검색어
     */
    private String searchKeyword;
}
