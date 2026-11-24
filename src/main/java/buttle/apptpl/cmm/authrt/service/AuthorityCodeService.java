package buttle.apptpl.cmm.authrt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.cmm.authrt.dto.SYS3010001Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3010002Dto;
import buttle.apptpl.cmm.authrt.mapper.AuthorityCodeMapper;
import buttle.apptpl.cmm.authrt.vo.SYS3010001Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3010002Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptAuthrtEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptAuthrtRepo;
import buttle.apptpl.common.util.ModelConveter;
import micoai.corebase.exception.InvalidationException;

import java.util.List;
import java.util.Optional;

/**
 * 사용자권한코드관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthorityCodeService {

    private final AuthorityCodeMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptAuthrtRepo cmptAuthrtRepo;

    /**
     * 사용자 권한 코드 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3010002Dto> selectUserAuthCdList(SYS3010001Dto dto) {
        SYS3010001Vo vo = modelConveter.map(dto, SYS3010001Vo.class);
        List<SYS3010002Vo> resultList = mapper.selectUserAuthCdList(vo);
        return modelConveter.mapList(resultList, SYS3010002Dto.class);
    }

    /**
     * 사용자 권한 코드 상세 조회
     */
    @Transactional(readOnly = true)
    public SYS3010002Dto selectUserAuthCdDtl(SYS3010001Dto dto) {
        SYS3010001Vo vo = modelConveter.map(dto, SYS3010001Vo.class);
        SYS3010002Vo result = mapper.selectUserAuthCdDtl(vo);
        return modelConveter.map(result, SYS3010002Dto.class);
    }

    /**
     * 사용자 권한 코드 저장
     */
    public SYS3010001Dto updateUserAuthCd(SYS3010002Dto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getAuthrtCd())) {
            throw new InvalidationException("권한 코드를 입력하세요.");
        }
        if (ObjectUtils.isEmpty(dto.getAuthrtNm())) {
            throw new InvalidationException("권한명을 입력하세요.");
        }

        // 기본값 설정
        if (ObjectUtils.isEmpty(dto.getUseYn())) {
            dto.setUseYn("Y");
        }
        if (ObjectUtils.isEmpty(dto.getSortSn())) {
            dto.setSortSn(0);
        }

        // 기존 데이터 확인
        Optional<CmptAuthrtEntity> existingEntityOpt = cmptAuthrtRepo.findById(dto.getAuthrtCd());

        CmptAuthrtEntity entity;
        if (existingEntityOpt.isPresent()) {
            // 수정: 기존 엔티티 업데이트 (PK 제외)
            entity = existingEntityOpt.get();
            BeanUtils.copyProperties(dto, entity, "authrtCd");
        } else {
            // 등록: 새 엔티티 생성
            entity = new CmptAuthrtEntity();
            BeanUtils.copyProperties(dto, entity);
        }

        cmptAuthrtRepo.save(entity);

        return modelConveter.map(entity, SYS3010001Dto.class);
    }

    /**
     * 사용자 권한 코드 삭제
     */
    public SYS3010001Dto deleteUserAuthCd(SYS3010002Dto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getAuthrtCd())) {
            throw new InvalidationException("권한 코드를 입력하세요.");
        }

        // 존재 여부 확인
        if (!cmptAuthrtRepo.existsById(dto.getAuthrtCd())) {
            throw new InvalidationException(String.format("삭제할 권한 데이터가 존재하지 않습니다. (%s)", dto.getAuthrtCd()));
        }

        cmptAuthrtRepo.deleteById(dto.getAuthrtCd());
        
        return modelConveter.map(dto, SYS3010001Dto.class);
    }
} 