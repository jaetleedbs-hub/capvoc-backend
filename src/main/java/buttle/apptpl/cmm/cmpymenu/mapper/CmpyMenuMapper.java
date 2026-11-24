package buttle.apptpl.cmm.cmpymenu.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.api.shared.vo.MenuListReqVo;
import buttle.apptpl.api.shared.vo.MenuListResVo;
import buttle.apptpl.api.shared.vo.MenuSaveReqVo;

import java.util.List;

/**
 * BO404S 메뉴 관리 Mapper
 */
@Mapper
public interface CmpyMenuMapper {

    /**
     * 메뉴 목록 조회
     *
     * @param reqVo 조회 조건
     * @return 메뉴 목록
     */
    List<MenuListResVo> selectMenuList(MenuListReqVo reqVo);

    /**
     * 메뉴 등록
     *
     * @param saveVo 저장 데이터
     * @return 저장된 행 수
     */
    int insertMenu(MenuSaveReqVo saveVo);

    /**
     * 메뉴 수정
     *
     * @param saveVo 저장 데이터
     * @return 수정된 행 수
     */
    int updateMenu(MenuSaveReqVo saveVo);

    /**
     * 회원사 유형별 전체 메뉴 삭제
     *
     * @param cmpyTypeCd 회원사 유형 코드
     * @return 삭제된 행 수
     */
    int deleteMenuAll(String cmpyTypeCd);
}
