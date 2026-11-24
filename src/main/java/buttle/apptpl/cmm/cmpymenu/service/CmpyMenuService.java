package buttle.apptpl.cmm.cmpymenu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import buttle.apptpl.api.shared.dto.*;
import buttle.apptpl.cmm.cmpymenu.mapper.CmpyMenuMapper;
import buttle.apptpl.api.shared.vo.MenuListReqVo;
import buttle.apptpl.api.shared.vo.MenuListResVo;
import buttle.apptpl.api.shared.vo.MenuSaveReqVo;
import buttle.apptpl.common.util.ModelConveter;

import java.util.List;

/**
 * BO404S 메뉴 관리 Service
 *
 * <p>회원사 유형별 메뉴를 관리합니다.</p>
 *
 * @since 2026-01-20
 * @author AICC 개발팀
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CmpyMenuService {

    private final CmpyMenuMapper bo404SMapper;
    private final ModelConveter modelConveter;

    /**
     * 메뉴 목록 조회
     *
     * @param dto 조회 요청 DTO
     * @return 메뉴 목록
     */
    @Transactional(readOnly = true)
    public List<MenuListResDto> getMenuList(MenuListReqDto dto) {
        log.info("메뉴 목록 조회 - cmpyTypeCd: {}", dto.getCmpyTypeCd());

        // 1. DTO -> VO 변환
        MenuListReqVo reqVo = modelConveter.map(dto, MenuListReqVo.class);

        // 2. Mapper를 통해 데이터 조회
        List<MenuListResVo> voList = bo404SMapper.selectMenuList(reqVo);

        // 3. VO -> DTO 변환 및 결과 반환
        List<MenuListResDto> result = modelConveter.mapList(voList, MenuListResDto.class);

        log.info("메뉴 목록 조회 완료 - cmpyTypeCd: {}, count: {}", 
                dto.getCmpyTypeCd(), result != null ? result.size() : 0);

        return result;
    }

    /**
     * 메뉴 저장 (등록/수정 통합 처리)
     *
     * @param dto 저장 요청 DTO
     * @return 저장 결과 DTO
     */
    public MenuSaveResDto saveMenu(MenuSaveReqDto dto) {
        log.info("메뉴 저장 시작 - menus count: {}", 
                dto.getMenus() != null ? dto.getMenus().size() : 0);

        try {
            if (dto.getMenus() == null || dto.getMenus().isEmpty()) {
                log.warn("저장할 메뉴 목록이 비어있습니다.");
                return new MenuSaveResDto("fail", "저장할 메뉴가 없습니다.");
            }

            // 회원사 유형 코드 추출 (첫 번째 메뉴 기준)
            String cmpyTypeCd = dto.getMenus().get(0).getCmpyTypeCd();

            // 1. 기존 메뉴 전체 삭제 (회원사 유형별)
            int deletedCount = bo404SMapper.deleteMenuAll(cmpyTypeCd);
            log.info("기존 메뉴 삭제 완료 - cmpyTypeCd: {}, deletedCount: {}", cmpyTypeCd, deletedCount);

            // 2. 새로운 메뉴 등록
            int insertedCount = 0;
            for (MenuSaveItemDto itemDto : dto.getMenus()) {
                // DTO -> VO 변환
                MenuSaveReqVo saveVo = modelConveter.map(itemDto, MenuSaveReqVo.class);
                
                // 메뉴 등록
                int result = bo404SMapper.insertMenu(saveVo);
                insertedCount += result;
            }

            log.info("메뉴 저장 완료 - cmpyTypeCd: {}, insertedCount: {}", cmpyTypeCd, insertedCount);

            return new MenuSaveResDto("success", "저장되었습니다.");

        } catch (Exception e) {
            log.error("메뉴 저장 중 오류 발생", e);
            throw new RuntimeException("메뉴 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
