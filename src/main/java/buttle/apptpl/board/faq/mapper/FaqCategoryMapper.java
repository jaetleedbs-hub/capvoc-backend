package buttle.apptpl.board.faq.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.api.shared.vo.CategoryDeleteReqVo;
import buttle.apptpl.api.shared.vo.CategoryListReqVo;
import buttle.apptpl.api.shared.vo.CategoryListResVo;
import buttle.apptpl.api.shared.vo.CategorySaveReqVo;

import java.util.List;

/**
 * BO302_P1 FAQ 분류 관리 Mapper
 */
@Mapper
public interface FaqCategoryMapper {
    
    /**
     * FAQ 분류 목록 조회
     */
    List<CategoryListResVo> selectCategoryList(CategoryListReqVo vo);
    
    /**
     * FAQ 분류 저장 (등록)
     */
    int insertCategory(CategorySaveReqVo vo);
    
    /**
     * FAQ 분류 저장 (수정)
     */
    int updateCategory(CategorySaveReqVo vo);
    
    /**
     * FAQ 분류 삭제
     */
    int deleteCategory(CategoryDeleteReqVo vo);
    
    /**
     * 코드 존재 여부 확인
     */
    int existsCategory(String cd);
    
    /**
     * 다음 정렬순서 조회
     */
    Integer selectNextSortOrder();
}
