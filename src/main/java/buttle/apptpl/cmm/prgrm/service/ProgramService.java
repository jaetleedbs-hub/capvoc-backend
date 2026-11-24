package buttle.apptpl.cmm.prgrm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.cmm.prgrm.dto.SYS3040001Dto;
import buttle.apptpl.cmm.prgrm.mapper.ProgramMapper;
import buttle.apptpl.cmm.prgrm.vo.SYS3040001Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptPrgrmEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptPrgrmRepo;
import buttle.apptpl.common.util.ModelConveter;
import micoai.corebase.exception.InvalidationException;

import java.util.List;
import java.util.Optional;

/**
 * 프로그램관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProgramService {

    private final ProgramMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptPrgrmRepo cmptPrgrmRepo;

    /**
     * API 등록
     * @param dtoList 프로그램등록 요청 목록
     * @return 처리 건수
     */
    public Integer updateApAuthList(List<SYS3040001Dto> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new InvalidationException("저장할 데이터가 없습니다.");
        }

        int processedCount = 0;
        for (SYS3040001Dto dto : dtoList) {
            // 필수 필드 검증
            if (ObjectUtils.isEmpty(dto.getPrgrmId())) {
                throw new InvalidationException("프로그램 ID를 입력하세요.");
            }
            if (ObjectUtils.isEmpty(dto.getPrgrmNm())) {
                throw new InvalidationException("프로그램 명을 입력하세요.");
            }

            // 기존 데이터 확인
            Optional<CmptPrgrmEntity> existingEntityOpt = cmptPrgrmRepo.findById(dto.getPrgrmId());

            CmptPrgrmEntity entity;
            if (existingEntityOpt.isPresent()) {
                // 수정: 기존 엔티티 업데이트 (PK 제외)
                entity = existingEntityOpt.get();
                BeanUtils.copyProperties(dto, entity, "prgrmId");
            } else {
                // 등록: 새 엔티티 생성
                entity = new CmptPrgrmEntity();
                entity.setPrgrmId(dto.getPrgrmId());
                BeanUtils.copyProperties(dto, entity);
            }

            cmptPrgrmRepo.save(entity);
            processedCount++;
        }

        return processedCount;
    }

    /**
     * API 조회
     * @param dto 프로그램등록 요청
     * @return 프로그램 목록
     */
    @Transactional(readOnly = true)
    public List<SYS3040001Dto> ApAuthList(SYS3040001Dto dto) {
        SYS3040001Vo vo = modelConveter.map(dto, SYS3040001Vo.class);
        List<SYS3040001Vo> result = mapper.ApAuthList(vo);
        return modelConveter.mapList(result, SYS3040001Dto.class);
    }

    /**
     * API 삭제
     * @param dtoList 프로그램삭제 요청 목록
     * @return 처리 건수
     */
    public Integer deleteApAuthList(List<SYS3040001Dto> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new InvalidationException("삭제할 데이터가 없습니다.");
        }

        int processedCount = 0;
        for (SYS3040001Dto dto : dtoList) {
            // 필수 필드 검증
            if (ObjectUtils.isEmpty(dto.getPrgrmId())) {
                throw new InvalidationException("프로그램 ID를 입력하세요.");
            }

            // 기존 데이터 확인
            Optional<CmptPrgrmEntity> existingEntityOpt = cmptPrgrmRepo.findById(dto.getPrgrmId());
            
            if (existingEntityOpt.isPresent()) {
                cmptPrgrmRepo.deleteById(dto.getPrgrmId());
                processedCount++;
            } else {
                throw new InvalidationException("삭제할 프로그램이 존재하지 않습니다: " + dto.getPrgrmId());
            }
        }

        return processedCount;
    }

} 