package buttle.apptpl.api.shared.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 상세 요청 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class FaqDetailReqVo {
    private String bbsId;
    private String pstId;
}
