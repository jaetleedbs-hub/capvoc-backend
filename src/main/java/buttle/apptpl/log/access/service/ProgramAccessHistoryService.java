package buttle.apptpl.log.access.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import buttle.apptpl.log.access.dto.SYS6040001Dto;
import buttle.apptpl.log.access.dto.SYS6040002Dto;
import buttle.apptpl.log.access.mapper.ProgramAccessHistoryMapper;
import buttle.apptpl.log.access.vo.SYS6040001Vo;
import buttle.apptpl.log.access.vo.SYS6040002Vo;
import buttle.apptpl.common.util.AuthOgnzUserUtils;
import buttle.apptpl.common.util.ModelConveter;
import micoai.corebase.exception.InvalidationException;

/**
 * SYS6040 프로그램접속이력 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramAccessHistoryService {

    private final ProgramAccessHistoryMapper mapper;
    private final ModelConveter modelConveter;

    /**
     * 프로그램 접속 이력 조회
     * @param dto 프로그램접속이력 요청
     * @return 프로그램접속이력 응답 목록
     */
    public Page<SYS6040002Dto> selectPgmConnHistList(SYS6040001Dto dto, Pageable pageable) {

        SYS6040001Vo vo = modelConveter.map(dto, SYS6040001Vo.class);
        if( vo == null){
            throw new InvalidationException("전달된 값이 올바르지 않습니다.");
        }

        vo.setUserId(AuthOgnzUserUtils.getAuthUserId(vo.getUserId()));

        Page<SYS6040002Vo> voList = mapper.selectPgmConnHistList(vo, pageable);
        
        return voList.map(voItem -> modelConveter.map(voItem, SYS6040002Dto.class));
    }

} 