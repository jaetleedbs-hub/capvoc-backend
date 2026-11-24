package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 메뉴 조회 요청 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuListReqVo {

    /** 회원사 유형 코드 */
    private String cmpyTypeCd;
}
