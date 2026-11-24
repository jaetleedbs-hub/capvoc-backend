package buttle.apptpl.cmm.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;
import buttle.apptpl.cmm.menu.dto.SYS1020001Dto;
import buttle.apptpl.cmm.menu.dto.SYS1020002Dto;
import buttle.apptpl.cmm.menu.mapper.MenuSearchMapper;
import buttle.apptpl.cmm.menu.vo.SYS1020001Vo;
import buttle.apptpl.cmm.menu.vo.SYS1020002Vo;
import buttle.apptpl.common.util.ModelConveter;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import micoai.corebase.exception.InvalidationException;

/**
 * 메뉴찾기 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuSearchService {

    private final MenuSearchMapper mapper;
    private final ModelConveter modelConveter;

    /**
     * 메뉴 조회
     * @param dto 메뉴찾기 요청 DTO
     * @return 메뉴찾기 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<SYS1020002Dto> selectMenuList(SYS1020001Dto dto) {
        SYS1020001Vo vo = modelConveter.map(dto, SYS1020001Vo.class);
        if (vo == null) {
            throw new InvalidationException("전달된 값이 올바르지 않습니다.");
        }

        SecUser user = SecurityUtils.getCurrentUser();
        vo.setAuthrtCd(user.getAuthrtCd());
        List<SYS1020002Vo> voList = mapper.selectMenuList(vo);
        return modelConveter.mapList(voList, SYS1020002Dto.class);
    }
} 