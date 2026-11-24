package buttle.apptpl.cmm.menu.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import buttle.apptpl.cmm.menu.vo.MenuByAuthLevelResVo;
import buttle.apptpl.cmm.menu.vo.SYS1010001Vo;
import buttle.apptpl.cmm.menu.vo.SYS1010002Vo;
import java.util.List;

/**
 * 메뉴관리 Mapper
 */
@Mapper
public interface MenuMapper {

    /**
     * 메뉴 목록 조회
     */
    List<SYS1010002Vo> selectMenuList(SYS1010001Vo param);

    /**
     * 권한등급별 메뉴 조회
     *
     * @param authLvl 권한등급(권한코드). null/빈값이면 전체
     * @param cmpyTypeCd 회원사유형코드. null/빈값이면 전체
     */
    List<MenuByAuthLevelResVo> selectMenuByAuthLevel(@Param("authLvl") String authLvl,
                                                    @Param("cmpyTypeCd") String cmpyTypeCd);
}
