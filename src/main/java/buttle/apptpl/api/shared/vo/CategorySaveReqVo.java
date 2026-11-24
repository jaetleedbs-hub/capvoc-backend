package buttle.apptpl.api.shared.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 분류 저장 요청 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class CategorySaveReqVo {
    private String cd;
    private String cdNm;
    private Integer sortOrder;
    private String regId;  // 등록자 ID
    private String updId;  // 수정자 ID
}
