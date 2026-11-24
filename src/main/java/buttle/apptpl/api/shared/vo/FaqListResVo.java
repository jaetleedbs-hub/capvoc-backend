package buttle.apptpl.api.shared.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 목록 응답 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class FaqListResVo {
    private String bbsId;
    private String pstId;
    private String categoryCd;
    private String categoryNm;
    private String question;
    private String displayYn;
    private String displayYnNm;
    private String viewCount;
    private String regDt;
    private String rgtrId;
    private String rgtrIdNm;
    private Integer totalCount;
}
