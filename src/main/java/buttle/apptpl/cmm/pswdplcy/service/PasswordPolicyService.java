package buttle.apptpl.cmm.pswdplcy.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.cmm.pswdplcy.dto.SYS5020001Dto;
import buttle.apptpl.cmm.pswdplcy.dto.SYS5020002Dto;
import buttle.apptpl.cmm.pswdplcy.mapper.PasswordPolicyMapper;
import buttle.apptpl.cmm.pswdplcy.vo.SYS5020001Vo;
import buttle.apptpl.cmm.pswdplcy.vo.SYS5020002Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdPlcyEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptPswdPlcyRepo;
import buttle.apptpl.common.util.ModelConveter;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordPolicyService {

    private final PasswordPolicyMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptPswdPlcyRepo cmptPswdPlcyRepo;

    /**
     * 비밀번호 정책 조회
     * @param dto 조회 조건
     * @return 비밀번호 정책
     */
    @Transactional(readOnly = true)
    public SYS5020002Dto selectPasswordPolicy(SYS5020001Dto dto) {
        // 정책 ID 고정값 설정
        BigDecimal pswdPlcyId = new BigDecimal("1");
        dto.setPswdPlcyId(pswdPlcyId);
        
        SYS5020001Vo vo = modelConveter.map(dto, SYS5020001Vo.class);
        SYS5020002Vo resultVo = mapper.selectPasswordPolicy(vo);
        return modelConveter.map(resultVo, SYS5020002Dto.class);
    }

    /**
     * 비밀번호 정책 저장
     * @param dto 저장할 비밀번호 정책 정보
     * @return 저장된 비밀번호 정책 정보
     */
    public SYS5020002Dto updatePasswordPolicy(SYS5020002Dto dto) {
        // 정책 ID 고정값 설정
        BigDecimal pswdPlcyId = new BigDecimal("1");
        dto.setPswdPlcyId(pswdPlcyId);

        // 기본값 설정
        if (ObjectUtils.isEmpty(dto.getPswdUcReqYn())) {
            dto.setPswdUcReqYn("N");
        }
        if (ObjectUtils.isEmpty(dto.getPswdLcReqYn())) {
            dto.setPswdLcReqYn("N");
        }
        if (ObjectUtils.isEmpty(dto.getPswdNumReqYn())) {
            dto.setPswdNumReqYn("N");
        }
        if (ObjectUtils.isEmpty(dto.getPswdSpcCharReqYn())) {
            dto.setPswdSpcCharReqYn("N");
        }

        // 엔티티 변환 및 저장
        CmptPswdPlcyEntity entity = new CmptPswdPlcyEntity();
        BeanUtils.copyProperties(dto, entity);
        // Note: rgtrId and mdfrId are auto-managed by BaseAuditingEntity
        cmptPswdPlcyRepo.save(entity);

        // 결과 반환
        SYS5020002Dto result = new SYS5020002Dto();
        BeanUtils.copyProperties(dto, result);
        return result;
    }

} 