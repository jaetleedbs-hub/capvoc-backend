package buttle.apptpl.api.shared.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FAQ 분류 목록 조회 요청 VO
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoryListReqVo {
    private String searchKeyword;
}

