package buttle.apptpl.api.shared.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 분류 응답 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class FaqCategoryResVo {
    private String cd;      // 분류 코드
    private String cdNm;    // 분류명
}

