package buttle.apptpl.cmm.menu.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 메뉴찾기 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS1020002Vo {

    private String menuId;
    private String menuNm;
    private String urlAddr;
    private String npagYn;
    private String upMenuId;
    private String upMenuIdNm;
    private Integer sortSn;
    private String sortPath;
    private String menuUseYn;
    private String scrnSize;
} 