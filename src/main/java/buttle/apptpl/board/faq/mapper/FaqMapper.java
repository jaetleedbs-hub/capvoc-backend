package buttle.apptpl.board.faq.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import buttle.apptpl.api.shared.vo.FaqCategoryResVo;
import buttle.apptpl.api.shared.vo.FaqDeleteReqVo;
import buttle.apptpl.api.shared.vo.FaqDetailReqVo;
import buttle.apptpl.api.shared.vo.FaqDetailResVo;
import buttle.apptpl.api.shared.vo.FaqListResVo;
import buttle.apptpl.api.shared.vo.FaqSaveReqVo;
import buttle.apptpl.api.shared.vo.FaqSearchReqVo;

import java.util.List;

/**
 * BO302_S 시스템 FAQ 관리 Mapper
 */
@Mapper
public interface FaqMapper {
    
    /**
     * FAQ 목록조회
     */
    Page<FaqListResVo> selectFaqList(FaqSearchReqVo vo, Pageable pageable);
    
    /**
     * FAQ 상세조회
     */
    FaqDetailResVo selectFaqDetail(FaqDetailReqVo vo);
    
    /**
     * FAQ 분류 목록 조회
     */
    List<FaqCategoryResVo> selectFaqCategory();

    /**
     * FAQ 총 건수 조회
    */ 
    int selectFaqListCount(FaqSearchReqVo vo);
    
    /**
     * FAQ 저장 (등록)
     */
    int insertFaq(FaqSaveReqVo vo);
    
    /**
     * FAQ 저장 (수정)
     */
    int updateFaq(FaqSaveReqVo vo);

    /**
     * FAQ 삭제
     * */
    int deleteFaqList(@Param("list") List<FaqDeleteReqVo> list);
}
