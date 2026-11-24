package buttle.apptpl.cmm.menu.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 메뉴관리 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS1010002Vo {

    private String menuId;

    private String menuNm;

    private String urlAddr;

    private String npagYn;

    private String upMenuId;

    private Integer sortSn;

    private String mdfrId;

    private String mdfrIdNm;

    private String mdfcnDt;

    private String scrnSize;

    private String menuUseYn;
} 