package buttle.apptpl.board.notice.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.api.shared.vo.BO301P1NoticeDetailReqVo;
import buttle.apptpl.api.shared.vo.BO301P1NoticeDetailResVo;

/**
 * BO301_P1 공지사항 상세 팝업 Mapper
 */
@Mapper
public interface NoticeDetailMapper {
    
    /**
     * 공지사항 상세조회
     */
    BO301P1NoticeDetailResVo selectNoticeDetail(BO301P1NoticeDetailReqVo vo);
    
    /**
     * 조회수 증가
     */
    int updateInqCnt(String pstId);
}
