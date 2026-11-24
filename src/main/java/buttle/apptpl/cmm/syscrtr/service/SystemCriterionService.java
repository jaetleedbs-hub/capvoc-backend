package buttle.apptpl.cmm.syscrtr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.cmm.syscrtr.dto.SYS5010001Dto;
import buttle.apptpl.cmm.syscrtr.dto.SYS5010002Dto;
import buttle.apptpl.cmm.syscrtr.mapper.SystemCriterionMapper;
import buttle.apptpl.cmm.syscrtr.vo.SYS5010001Vo;
import buttle.apptpl.cmm.syscrtr.vo.SYS5010002Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptSysCrtrEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptSysCrtrRepo;
import buttle.apptpl.common.util.ModelConveter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SystemCriterionService {

    private final SystemCriterionMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptSysCrtrRepo cmptSysCrtrRepo;

    /**
     * 기준 정보 조회
     * @param dto 조회 조건
     * @return 기준 정보 목록
     */
    @Transactional(readOnly = true)
    public List<SYS5010002Dto> selectBaseInfoList(SYS5010001Dto dto) {
        SYS5010001Vo vo = modelConveter.map(dto, SYS5010001Vo.class);
        List<SYS5010002Vo> voList = mapper.selectBaseInfoList(vo);
        return modelConveter.mapList(voList, SYS5010002Dto.class);
    }

    /**
     * 기준 정보 상세 조회
     * @param dto 조회 조건
     * @return 기준 정보 상세
     */
    @Transactional(readOnly = true)
    public SYS5010002Dto selectBaseInfoDetail(SYS5010001Dto dto) {
        SYS5010001Vo vo = modelConveter.map(dto, SYS5010001Vo.class);
        SYS5010002Vo resultVo = mapper.selectBaseInfoDetail(vo);
        return modelConveter.map(resultVo, SYS5010002Dto.class);
    }

    /**
     * 기준 정보 엑셀 출력
     * @param dto 조회 조건
     * @return 엑셀 데이터
     */
    public SYS5010001Dto exportExcelBaseInfoList(SYS5010001Dto dto) {
        // 엑셀 출력 로직 구현
        // TODO: 엑셀 생성 로직 추가
        return dto;
    }

    /**
     * 기준 정보 저장 (등록/수정)
     * @param dto 저장할 데이터
     * @return 저장 결과
     */
    @Transactional
    public SYS5010001Dto updateBaseInfo(SYS5010002Dto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getSysCrtrTypeCd())) {
            throw new InvalidationException("기준유형은 필수 선택 항목입니다.");
        }
        if (ObjectUtils.isEmpty(dto.getUseYn())) {
            throw new InvalidationException("사용여부는 필수 선택 항목입니다.");
        }
        if (ObjectUtils.isEmpty(dto.getSysCrtrCdNm())) {
            throw new InvalidationException("기준명은 필수 입력 항목입니다.");
        }

        // 기존 데이터 존재 여부 확인
        Optional<CmptSysCrtrEntity> existingEntity = cmptSysCrtrRepo.findById(dto.getSysCrtrCd());
        
        CmptSysCrtrEntity entity;
        
        // 엔티티 변환 및 저장
        if (existingEntity.isPresent()) {
            // 수정
            entity = existingEntity.get();
            BeanUtils.copyProperties(dto, entity);
        } else {
            // 신규 등록
            entity = new CmptSysCrtrEntity();
            BeanUtils.copyProperties(dto, entity);
        }
        cmptSysCrtrRepo.save(entity);

        // 결과 반환
        SYS5010001Dto result = new SYS5010001Dto();
        BeanUtils.copyProperties(dto, result);
        return result;
    }

    /**
     * 기준 정보 삭제
     * @param dto 삭제할 데이터
     * @return 삭제 결과
     */
    @Transactional
    public SYS5010001Dto deleteBaseInfo(SYS5010002Dto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getSysCrtrCd())) {
            throw new InvalidationException("시스템 기준 코드를 입력하세요.");
        }

        // 삭제 대상 존재 여부 확인
        if (!cmptSysCrtrRepo.existsById(dto.getSysCrtrCd())) {
            throw new InvalidationException("삭제할 데이터가 존재하지 않습니다.");
        }

        // 삭제
        cmptSysCrtrRepo.deleteById(dto.getSysCrtrCd());

        // 결과 반환
        SYS5010001Dto result = new SYS5010001Dto();
        BeanUtils.copyProperties(dto, result);
        return result;
    }
} 