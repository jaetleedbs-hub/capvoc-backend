package buttle.apptpl.api.shared.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 저장 요청 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class FaqSaveReqVo {
    private String bbsId;
    private String pstId;
    private String categoryCd;
    private String question;
    private String answer;
    private String displayYn;
    private Integer sortOrder;
    private String rgtrId;  // 등록자 ID
    private String mdfrId;  // 수정자 ID
}
