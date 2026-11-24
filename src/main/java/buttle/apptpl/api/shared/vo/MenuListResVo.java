package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 메뉴 조회 응답 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuListResVo {

    /** 회원사 유형 코드 */
    private String cmpyTypeCd;

    /** 메뉴 ID */
    private String menuId;

    /** 메뉴명 */
    private String menuNm;

    /** URL 주소 */
    private String urlAddr;

    /** 새창 여부 */
    private String npagYn;

    /** 상위 메뉴 ID */
    private String upMenuId;

    /** 정렬 순번 */
    private Integer sortSn;

    /** 화면 크기 */
    private String scrnSize;

    /** 메뉴 사용 여부 */
    private String menuUseYn;
}
