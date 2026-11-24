package buttle.apptpl.cmm.menu.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.menu.vo.SYS1020001Vo;
import buttle.apptpl.cmm.menu.vo.SYS1020002Vo;

import java.util.List;

/**
 * 메뉴찾기 Mapper
 */
@Mapper
public interface MenuSearchMapper {

    /**
     * 메뉴 조회
     * @param vo 메뉴찾기 요청 VO
     * @return 메뉴찾기 결과 VO 리스트
     */
    List<SYS1020002Vo> selectMenuList(SYS1020001Vo vo);
} 