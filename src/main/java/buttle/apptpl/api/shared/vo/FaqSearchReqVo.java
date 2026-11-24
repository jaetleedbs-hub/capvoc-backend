package buttle.apptpl.api.shared.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 검색 요청 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class FaqSearchReqVo {
    private String categoryCd;
    private String displayYn;
    private String startDate;
    private String endDate;
    private String searchKeyword;
    private Integer offset;
    private Integer limit;
}
