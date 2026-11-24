package buttle.apptpl.cmm.menu.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 권한등급별 메뉴 조회 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MenuByAuthLevelResVo {

    private String cmpyTypeCd;
    private String menuId;
    private String menuNm;
    /** cmpt_cmpy_menu.url_addr — 소비 측(desk MenuInfoVo) 필드명이 tmpltUrlAddr 이라 그대로 맞춘다. */
    private String tmpltUrlAddr;
    private String npagYn;
    private String upMenuId;
    private Integer sortSn;
    private String menuUseYn;
    private String scrnSize;
    private String authrtCd;
    private String authrtNm;
    private String authrtSe;
    private Integer menuLvl;
}
