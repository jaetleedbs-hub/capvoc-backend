package buttle.apptpl.cmm.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.cmm.menu.dto.MenuByAuthLevelResDto;
import buttle.apptpl.cmm.menu.dto.SYS1010001Dto;
import buttle.apptpl.cmm.menu.dto.SYS1010002Dto;
import buttle.apptpl.cmm.menu.mapper.MenuMapper;
import buttle.apptpl.cmm.menu.vo.MenuByAuthLevelResVo;
import buttle.apptpl.cmm.menu.vo.SYS1010001Vo;
import buttle.apptpl.cmm.menu.vo.SYS1010002Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptMenuEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptMenuRepo;
import buttle.apptpl.common.util.ModelConveter;
import micoai.corebase.exception.InvalidationException;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 메뉴관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptMenuRepo cmptMenuRepo;
    private static final Logger logger = LoggerFactory.getLogger(MenuService.class);

    /**
     * 메뉴 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS1010002Dto> selectMenuList(SYS1010001Dto dto) {
        SYS1010001Vo vo = modelConveter.map(dto, SYS1010001Vo.class);
        List<SYS1010002Vo> resultList = mapper.selectMenuList(vo);
        return modelConveter.mapList(resultList, SYS1010002Dto.class);
    }

    /**
     * 권한등급별 메뉴 조회
     *
     * <p>상담사 콘솔(desk-backend)이 서버 간 호출로 사용한다.
     * 메뉴·권한 매핑은 admin 스키마에만 있으므로 desk 가 직접 조회할 수 없다.</p>
     *
     * @param authLvl 권한등급(권한코드). null 이면 전체
     * @param cmpyTypeCd 회원사유형코드. null 이면 전체
     */
    @Transactional(readOnly = true)
    public List<MenuByAuthLevelResDto> selectMenuByAuthLevel(String authLvl, String cmpyTypeCd) {
        List<MenuByAuthLevelResVo> resultList = mapper.selectMenuByAuthLevel(authLvl, cmpyTypeCd);
        return modelConveter.mapList(resultList, MenuByAuthLevelResDto.class);
    }

    /**
     * 메뉴 저장 (등록/수정)
     */
    public Integer updateMenuList(List<SYS1010002Dto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new InvalidationException("저장할 메뉴 데이터가 없습니다.");
        }

        for (SYS1010002Dto dto : dtoList) {
            if (ObjectUtils.isEmpty(dto.getMenuId())) {
                throw new InvalidationException("메뉴 ID를 입력하세요.");
            }
            if (ObjectUtils.isEmpty(dto.getMenuNm())) {
                throw new InvalidationException("메뉴명을 입력하세요.");
            }

            // 기본값 설정
            if (ObjectUtils.isEmpty(dto.getNpagYn())) {
                dto.setNpagYn("X");
            }
            if (ObjectUtils.isEmpty(dto.getSortSn())) {
                dto.setSortSn(0);
            }

            // 복합키 생성 및 기존 데이터 확인
            Optional<CmptMenuEntity> existingEntityOpt = cmptMenuRepo.findById(dto.getMenuId());

            CmptMenuEntity entity;
            if (existingEntityOpt.isPresent()) {
                // 수정: 기존 엔티티 업데이트 (PK 제외)
                entity = existingEntityOpt.get();
                BeanUtils.copyProperties(dto, entity, "menuId", "rowtype");
            } else {
                // 등록: 새 엔티티 생성
                entity = new CmptMenuEntity();
                BeanUtils.copyProperties(dto, entity, "rowtype");
            }

            cmptMenuRepo.save(entity);
        }

        return dtoList.size();
    }

    /**
     * 메뉴 삭제
     */
    public Integer deleteMenuList(List<SYS1010002Dto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new InvalidationException("삭제할 메뉴 데이터가 없습니다.");
        }

        for (SYS1010002Dto dto : dtoList) {
            logger.info(String.format("🔍 메뉴ID: %s", dto.getMenuId()));
        }

        int deletedCount = 0;

        // 전체 메뉴를 한 번만 조회
        List<CmptMenuEntity> allMenus = cmptMenuRepo.findAll();

        for (SYS1010002Dto dto : dtoList) {
            if (ObjectUtils.isEmpty(dto.getMenuId())) {
                throw new InvalidationException("삭제할 메뉴 ID를 입력하세요.");
            }

            // findAll로 자식 메뉴 존재 여부 확인
            boolean hasChild = allMenus.stream()
                .anyMatch(menu -> dto.getMenuId().equals(menu.getUpMenuId()));
            if (hasChild) {
                throw new InvalidationException("자식 메뉴가 존재합니다. 먼저 자식 메뉴를 삭제하세요. (메뉴ID: " + dto.getMenuId() + ")");
            }

            // 메뉴 ID로 삭제 처리
            Optional<CmptMenuEntity> existingEntityOpt = cmptMenuRepo.findById(dto.getMenuId());
            if (existingEntityOpt.isPresent()) {
                 cmptMenuRepo.delete(existingEntityOpt.get());
                deletedCount++;
                logger.info("✅ 메뉴 삭제 완료: " + dto.getMenuId());
             } else {
                logger.error("⚠️ 삭제할 메뉴를 찾을 수 없습니다: " + dto.getMenuId());
             }
        }
        return deletedCount;
    }
} 