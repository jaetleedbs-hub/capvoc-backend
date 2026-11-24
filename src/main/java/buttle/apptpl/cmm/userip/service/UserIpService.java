package buttle.apptpl.cmm.userip.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.cmm.userip.dto.SYS0080001Dto;
import buttle.apptpl.cmm.userip.dto.SYS0080002Dto;
import buttle.apptpl.cmm.userip.mapper.UserIpMapper;
import buttle.apptpl.cmm.userip.vo.SYS0080001Vo;
import buttle.apptpl.cmm.userip.vo.SYS0080002Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptIpEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptIpRepo;
import buttle.apptpl.common.util.ModelConveter;

/**
 * 사용자IP관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserIpService {

    private final UserIpMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptIpRepo cmptIpRepo;

    /**
     * 사용자 IP별 내선정보 조회
     * @param dto 사용자IP관리 요청 DTO
     * @return 사용자IP관리 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<SYS0080002Dto> selectUserIpTypeExtInfoList(SYS0080001Dto dto) {
        SYS0080001Vo vo = modelConveter.map(dto, SYS0080001Vo.class);
        List<SYS0080002Vo> voList = mapper.selectUserIpTypeExtInfoList(vo);
        return modelConveter.mapList(voList, SYS0080002Dto.class);
    }

    /**
     * 사용자 IP별 내선정보 저장
     * @param dto 사용자IP관리 요청 DTO
     * @return 사용자IP관리 응답 DTO
     */
    public Integer updateUserIpTypeExtInfoList(List<SYS0080002Dto> dtoList) {
        if (ObjectUtils.isEmpty(dtoList)) {
            throw new InvalidationException("저장할 근태 정보가 없습니다.");
        }

        for (SYS0080002Dto dto : dtoList) {
            // 필수 필드 검증
            if (ObjectUtils.isEmpty(dto.getLgnIp())) {
                throw new InvalidationException("로그인 IP를 입력하세요.");
            }

            // 기존 데이터 확인
            Optional<CmptIpEntity> existingEntityOpt = cmptIpRepo.findById(dto.getLgnIp());

            CmptIpEntity entity;
            if (existingEntityOpt.isPresent()) {
                // 수정: 기존 엔티티 업데이트 (PK 제외)
                entity = existingEntityOpt.get();
                BeanUtils.copyProperties(dto, entity, "lgnIp");
            } else {
                // 등록: 새 엔티티 생성
                entity = new CmptIpEntity();
                BeanUtils.copyProperties(dto, entity);
            }

            cmptIpRepo.save(entity);
        }
        
        return dtoList.size();
    }

    /**
     * 사용자 IP별 내선정보 삭제
     * @param dtoList 삭제할 사용자IP관리 DTO 리스트
     * @return 삭제된 행 수
     */
    public Integer deleteUserIpTypeExtInfoList(List<SYS0080002Dto> dtoList) {
        if (ObjectUtils.isEmpty(dtoList)) {
            throw new InvalidationException("삭제할 사용자 IP 정보가 없습니다.");
        }

        int deletedCount = 0;
        for (SYS0080002Dto dto : dtoList) {
            // 필수 필드 검증
            if (ObjectUtils.isEmpty(dto.getLgnIp())) {
                throw new InvalidationException("삭제할 로그인 IP를 입력하세요.");
            }

            // 기존 데이터 확인
            Optional<CmptIpEntity> existingEntityOpt = cmptIpRepo.findById(dto.getLgnIp());

            if (existingEntityOpt.isPresent()) {
                // 삭제 실행
                cmptIpRepo.deleteById(dto.getLgnIp());
                deletedCount++;
            } else {
                throw new InvalidationException("삭제할 로그인 IP [" + dto.getLgnIp() + "]가 존재하지 않습니다.");
            }
        }
        
        return deletedCount;
    }
} 