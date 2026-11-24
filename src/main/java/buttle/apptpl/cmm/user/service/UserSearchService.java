package buttle.apptpl.cmm.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import buttle.apptpl.cmm.user.dto.SYS0040001Dto;
import buttle.apptpl.cmm.user.dto.SYS0040002Dto;
import buttle.apptpl.cmm.user.dto.SYS0040003Dto;
import buttle.apptpl.cmm.user.mapper.UserSearchMapper;
import buttle.apptpl.cmm.user.vo.SYS0040001Vo;
import buttle.apptpl.cmm.user.vo.SYS0040002Vo;
import buttle.apptpl.cmm.user.vo.SYS0040003Vo;
import buttle.apptpl.common.util.AuthOgnzUserUtils;
import buttle.apptpl.common.util.ModelConveter;
import micoai.corebase.exception.InvalidationException;
/**
 * 사용자찾기 Service
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserSearchService {

    private final UserSearchMapper mapper;
    private final ModelConveter modelConveter;
    
    /**
     * 사용자 소속 조직 조회
     * @param dto 검색 조건
     * @return 조직 목록
     */
    public List<SYS0040002Dto> selectUserBelongOrgList(SYS0040001Dto dto) {
        SYS0040001Vo vo = modelConveter.map(dto, SYS0040001Vo.class);
        List<SYS0040002Vo> resultList = mapper.selectUserBelongOrgList(vo);
        return modelConveter.mapList(resultList, SYS0040002Dto.class);
    }

    /**
     * 조직별 사용자 정보 조회
     * @param dto 검색 조건
     * @return 사용자 목록
     */
    public List<SYS0040003Dto> selectOrgTypeUserInfoList(SYS0040001Dto dto) {
        SYS0040001Vo vo = modelConveter.map(dto, SYS0040001Vo.class);
        if(vo == null){
            throw new InvalidationException("전달된 값이 올바르지 않습니다.");
        }

        // 권한 예외 여부가 true이면 모든 데이터가 조회되도록 한다.
        if(!"false".equalsIgnoreCase(vo.getAuthrt())) {
            vo.setOgnzCd(AuthOgnzUserUtils.getAuthOgnzCd(vo.getOgnzCd()));
            // 조회조건에는 없으나 권한에 맞는 사용자 조회 조건 처리위하여 설정
            vo.setUserId(AuthOgnzUserUtils.getAuthUserId());
        }


        List<SYS0040003Vo> resultList = mapper.selectOrgTypeUserInfoList(vo);
        return modelConveter.mapList(resultList, SYS0040003Dto.class);
    }
} 