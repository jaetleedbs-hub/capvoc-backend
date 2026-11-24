package buttle.apptpl.api.shared.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 상세 응답 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class FaqDetailResVo {
    private String bbsId;
    private String pstId;
    private String categoryCd;
    private String categoryNm;
    private String question;
    private String answer;
    private String displayYn;
    private Integer sortOrder;
    private String regDt;
    private String rgtrId;
    private String updDt;
    private String mdfrId;
}
