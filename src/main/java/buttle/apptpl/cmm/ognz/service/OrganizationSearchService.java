package buttle.apptpl.cmm.ognz.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import buttle.apptpl.api.common.components.service.CommonUserAuthService;
import buttle.apptpl.cmm.ognz.dto.SYS0020001Dto;
import buttle.apptpl.cmm.ognz.dto.SYS0020002Dto;
import buttle.apptpl.cmm.ognz.mapper.OrganizationSearchMapper;
import buttle.apptpl.cmm.ognz.vo.SYS0020001Vo;
import buttle.apptpl.cmm.ognz.vo.SYS0020002Vo;
import buttle.apptpl.common.util.AuthOgnzUserUtils;
import buttle.apptpl.common.util.ModelConveter;
import micoai.corebase.exception.InvalidationException;

/**
 * 조직찾기 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationSearchService {

    private final OrganizationSearchMapper mapper;
    private final ModelConveter modelConveter;

    /**
     * 조직 조회
     */
    public List<SYS0020002Dto> selectOrgList(SYS0020001Dto dto) {
        SYS0020001Vo vo = modelConveter.map(dto, SYS0020001Vo.class);

        if(vo == null){
            throw new InvalidationException("전달된 값이 올바르지 않습니다.");
        }

        if( !"false".equals(dto.getAuthrt())) {
            vo.setOgnzCd(AuthOgnzUserUtils.getAuthOgnzCd(vo.getOgnzCd()));
        }

        List<SYS0020002Vo> resultList = mapper.selectOrgList(vo);
        return modelConveter.mapList(resultList, SYS0020002Dto.class);
    }

} 