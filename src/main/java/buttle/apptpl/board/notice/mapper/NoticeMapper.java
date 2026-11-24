package buttle.apptpl.board.notice.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.api.shared.vo.*;

import java.util.List;

/**
 * BO301_S 시스템 공지사항 관리 Mapper
 */
@Mapper
public interface NoticeMapper {
    
    /**
     * 공지사항 목록조회
     */
    List<BO301SNoticeListResVo> selectNoticeList(BO301SNoticeSearchReqVo vo);
    
    /**
     * 공지사항 상세조회
     */
    BO301SNoticeDetailResVo selectNoticeDetail(String pstId);
    
    /**
     * 공지사항 등록
     */
    int insertNotice(BO301SNoticeSaveReqVo vo);
    
    /**
     * 공지사항 수정
     */
    int updateNotice(BO301SNoticeSaveReqVo vo);
    
    /**
     * 공지사항 삭제
     */
    int deleteNotice(String pstId);
    
    /**
     * 공지사항 존재 여부 확인
     */
    int selectNoticeExists(String pstId);
    
    /**
     * 공지사항 조회수 증가
     */
    int updateInquiryCount(String pstId);
}
