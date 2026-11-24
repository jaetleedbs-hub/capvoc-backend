package buttle.apptpl.board.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.api.shared.dto.NoticePopupDetailReqDto;
import buttle.apptpl.api.shared.dto.NoticePopupDetailResDto;
import buttle.apptpl.board.notice.mapper.NoticeDetailMapper;
import buttle.apptpl.api.shared.vo.BO301P1NoticeDetailReqVo;
import buttle.apptpl.api.shared.vo.BO301P1NoticeDetailResVo;
import micoai.corebase.exception.InvalidationException;

/**
 * BO301_P1 공지사항 상세 팝업 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeDetailService {

    private final NoticeDetailMapper mapper;

    /**
     * 공지사항 상세조회 (팝업)
     */
    public NoticePopupDetailResDto getNoticeDetail(NoticePopupDetailReqDto dto) {
        log.debug("공지사항 상세조회 시작 - pstId: {}", dto.getPstId());

        if (ObjectUtils.isEmpty(dto.getPstId())) {
            throw new InvalidationException("게시물ID를 입력해 주세요.");
        }

        try {
            BO301P1NoticeDetailReqVo reqVo = new BO301P1NoticeDetailReqVo();
            BeanUtils.copyProperties(dto, reqVo);
            
            // 상세 조회
            BO301P1NoticeDetailResVo vo = mapper.selectNoticeDetail(reqVo);
            
            if (vo == null) {
                throw new InvalidationException("해당 공지사항을 찾을 수 없습니다.");
            }

            // 조회수 증가
            mapper.updateInqCnt(dto.getPstId());

            NoticePopupDetailResDto result = new NoticePopupDetailResDto();
            BeanUtils.copyProperties(vo, result);
            
            return result;

        } catch (Exception e) {
            log.error("공지사항 상세조회 실패", e);
            throw new InvalidationException("공지사항 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
}
