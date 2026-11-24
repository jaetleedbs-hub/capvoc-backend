package buttle.apptpl.log.error.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import buttle.apptpl.common.util.ModelConveter;
import buttle.apptpl.log.error.dto.SYS6020001Dto;
import buttle.apptpl.log.error.dto.SYS6020002Dto;
import buttle.apptpl.log.error.dto.SYS6020003Dto;
import buttle.apptpl.log.error.mapper.ErrorLogMapper;
import buttle.apptpl.log.error.vo.SYS6020001Vo;
import buttle.apptpl.log.error.vo.SYS6020002Vo;
import buttle.apptpl.log.error.vo.SYS6020003Vo;
import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;

/**
 * SYS6020 오류로그 Service
 *
 * <p>형제 화면(SYS6040 프로그램접속이력)과 <b>같은 모양</b>이다 — dto→vo 로 옮겨 매퍼를 부르고
 * 결과를 다시 dto 로 옮긴다. 새 패턴을 만들지 않는다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ErrorLogService {

    private final ErrorLogMapper mapper;
    private final ModelConveter modelConveter;

    /** 오류로그 목록. */
    public Page<SYS6020002Dto> selectErrorLogList(SYS6020001Dto dto, Pageable pageable) {

        SYS6020001Vo vo = modelConveter.map(dto, SYS6020001Vo.class);
        if (vo == null) {
            throw new InvalidationException("전달된 값이 올바르지 않습니다.");
        }

        Page<SYS6020002Vo> voList = mapper.selectErrorLogList(vo, pageable);

        return voList.map(voItem -> modelConveter.map(voItem, SYS6020002Dto.class));
    }

    /**
     * 오류로그 상세.
     *
     * <p>없는 것을 <b>빈 값으로 돌려주지 않는다</b> — 「없다」와 「못 읽었다」가 화면에서 같아 보이면
     * 오늘 이 표가 없애려는 병이 화면에 다시 생긴다.
     */
    public SYS6020003Dto selectErrorLog(Integer logSid) {
        if (logSid == null) {
            throw new InvalidationException("전달된 값이 올바르지 않습니다.");
        }

        SYS6020003Vo vo = mapper.selectErrorLog(logSid);
        if (vo == null) {
            throw new InvalidationException("해당 오류로그를 찾을 수 없습니다.");
        }

        return modelConveter.map(vo, SYS6020003Dto.class);
    }
}
