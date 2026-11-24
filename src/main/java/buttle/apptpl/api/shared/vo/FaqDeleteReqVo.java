package buttle.apptpl.api.shared.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 삭제 요청 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class FaqDeleteReqVo {
    private String bbsId;
    private String pstId;
}
