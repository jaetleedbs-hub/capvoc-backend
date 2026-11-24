package buttle.apptpl.cmm.ognz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.cmm.ognz.dto.SYS0010001Dto;
import buttle.apptpl.cmm.ognz.dto.SYS0010002Dto;
import buttle.apptpl.cmm.ognz.dto.SYS0010003Dto;
import buttle.apptpl.cmm.ognz.mapper.OrganizationMapper;
import buttle.apptpl.cmm.ognz.vo.SYS0010001Vo;
import buttle.apptpl.cmm.ognz.vo.SYS0010002Vo;
import buttle.apptpl.cmm.ognz.vo.SYS0010003Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptOgnzEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptOgnzRepo;
import buttle.apptpl.common.util.ModelConveter;
import micoai.corebase.exception.InvalidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 조직관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrganizationService {

    private final OrganizationMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptOgnzRepo cmptOgnzRepo;

    
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 조직 조회
     */
    @Transactional(readOnly = true)
    public List<SYS0010002Dto> selectOrgList(SYS0010001Dto dto) {
        SYS0010001Vo vo = modelConveter.map(dto, SYS0010001Vo.class);
        List<SYS0010002Vo> resultList = mapper.selectOrgList(vo);
        return modelConveter.mapList(resultList, SYS0010002Dto.class);
    }

    /**
     * 조직 상세 정보 조회
     */
    @Transactional(readOnly = true)
    public SYS0010003Dto selectOrgDtlInfo(SYS0010001Dto dto) {
        SYS0010001Vo vo = modelConveter.map(dto, SYS0010001Vo.class);
        SYS0010003Vo result = mapper.selectOrgDtlInfo(vo);
        return modelConveter.map(result, SYS0010003Dto.class);
    }

    /**
     * 조직 저장
     */
    public SYS0010001Dto updateOrg(SYS0010003Dto dto) {
        log.info("조직 저장 시작: 조직코드={}, 조직명={}", dto.getOgnzCd(), dto.getOgnzNm());
        
        // 기존 데이터 확인
        Optional<CmptOgnzEntity> existingEntityOpt = cmptOgnzRepo.findById(dto.getOgnzCd());
        
        // 필수값 검증
        if (ObjectUtils.isEmpty(dto.getOgnzCd())) {
            throw new InvalidationException("조직 코드를 입력하세요.");
        }
        if (ObjectUtils.isEmpty(dto.getOgnzNm())) {
            throw new InvalidationException("조직명을 입력하세요.");
        }
        
        // 상위 조직 설정 처리
        if (!ObjectUtils.isEmpty(dto.getUpOgnzCd())) {
            // 자동 조직 레벨 설정
            if (dto.getOgnzLvl() == null) {
                Optional<CmptOgnzEntity> parentOrgOpt = cmptOgnzRepo.findById(dto.getUpOgnzCd());
                if (parentOrgOpt.isPresent()) {
                    CmptOgnzEntity parentOrg = parentOrgOpt.get();
                    if (parentOrg.getOgnzLvl() == null) {
                        parentOrg.setOgnzLvl(1);
                    }
                    dto.setOgnzLvl(BigDecimal.valueOf(parentOrg.getOgnzLvl()).add(BigDecimal.ONE));
                }
            }
        } else {
            // 최상위 조직인 경우 레벨 1 설정
            if (dto.getOgnzLvl() == null) {
                dto.setOgnzLvl(BigDecimal.ONE);
            }
        }

        CmptOgnzEntity entity;
        if (existingEntityOpt.isPresent()) {
            // 수정: 기존 엔티티 업데이트 (PK 제외)
            entity = existingEntityOpt.get();
            BeanUtils.copyProperties(dto, entity, "ognzCd", "ognzLvl", "sortSn");
            setIntegerFields(dto, entity);
            
            // 상위 조직 참조 설정
            if (!ObjectUtils.isEmpty(dto.getUpOgnzCd())) {
                Optional<CmptOgnzEntity> parentOrgOpt = cmptOgnzRepo.findById(dto.getUpOgnzCd());
                if (parentOrgOpt.isPresent()) {
                    entity.setParent(parentOrgOpt.get());
                }
            } else {
                entity.setParent(null);
            }
            
            log.info("조직 수정 처리: 조직코드={}", dto.getOgnzCd());
        } else {
            // 등록: 새 엔티티 생성
            entity = new CmptOgnzEntity();
            BeanUtils.copyProperties(dto, entity, "ognzLvl", "sortSn");
            setIntegerFields(dto, entity);
            
            // 상위 조직 참조 설정
            if (!ObjectUtils.isEmpty(dto.getUpOgnzCd())) {
                Optional<CmptOgnzEntity> parentOrgOpt = cmptOgnzRepo.findById(dto.getUpOgnzCd());
                if (parentOrgOpt.isPresent()) {
                    entity.setParent(parentOrgOpt.get());
                }
            }
            
            log.info("조직 신규 등록 처리: 조직코드={}", dto.getOgnzCd());
        }

        cmptOgnzRepo.save(entity);
        
        log.info("조직 저장 완료: 조직코드={}, 조직명={}", entity.getOgnzCd(), entity.getOgnzNm());
        
        return modelConveter.map(entity, SYS0010001Dto.class);
    }

    private void setIntegerFields(SYS0010003Dto dto, CmptOgnzEntity entity) {
        entity.setOgnzLvl(toInt4(dto.getOgnzLvl(), "조직 레벨"));
        entity.setSortSn(toInt4(dto.getSortSn(), "정렬 순번"));
    }

    private Integer toInt4(BigDecimal value, String fieldName) {
        if (value == null) {
            return null;
        }
        try {
            return value.intValueExact();
        } catch (ArithmeticException e) {
            throw new InvalidationException(fieldName + "은(는) 정수 범위 값이어야 합니다.");
        }
    }

    /**
     * 조직 삭제
     */
    public SYS0010001Dto deleteOrg(SYS0010001Dto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getOgnzCd())) {
            throw new InvalidationException("조직 코드를 입력하세요.");
        }

        // 존재 여부 확인
        if (!cmptOgnzRepo.existsById(dto.getOgnzCd())) {
            throw new InvalidationException(String.format("삭제할 조직 데이터가 존재하지 않습니다. (%s)", dto.getOgnzCd()));
        }

        // 하위 조직 존재 여부 확인
        boolean hasChildOrg = cmptOgnzRepo.findAll().stream()
        .anyMatch(org -> org.getParent() != null && dto.getOgnzCd().equals(org.getParent().getOgnzCd()));
    
        if (hasChildOrg) {
            throw new InvalidationException(String.format("하위 조직이 존재하여 삭제할 수 없습니다. (조직코드: %s)", dto.getOgnzCd()));
        }
        cmptOgnzRepo.deleteById(dto.getOgnzCd());
        
        return dto;
    }

    
} 