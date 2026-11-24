package buttle.apptpl.api.shared.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 분류 목록 응답 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoryListResVo {
    private String cd;
    private String cdNm;
    private String cdGroup;
    private Integer sortOrder;
    private String useYn;
}
