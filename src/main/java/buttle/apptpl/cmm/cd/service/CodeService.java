package buttle.apptpl.cmm.cd.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.cmm.cd.dto.SYS5040001Dto;
import buttle.apptpl.cmm.cd.dto.SYS5040002Dto;
import buttle.apptpl.cmm.cd.dto.SYS5040003Dto;
import buttle.apptpl.cmm.cd.dto.SYS5040004Dto;
import buttle.apptpl.cmm.cd.dto.SYS5040005Dto;
import buttle.apptpl.cmm.cd.mapper.CodeMapper;
import buttle.apptpl.common.i18n.LocaleSupport;
import buttle.apptpl.cmm.cd.vo.SYS5040001Vo;
import buttle.apptpl.cmm.cd.vo.SYS5040003Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptCdEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptCdGroupEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptCdPkey;
import buttle.apptpl.common.model.cmpt.entity.CmptRfrncCdEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptRfrncCdPkey;
import buttle.apptpl.common.model.cmpt.repository.CmptCdGroupRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptCdRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptRfrncCdRepo;
import buttle.apptpl.common.util.ModelConveter;

@Service
@RequiredArgsConstructor
@Transactional
public class CodeService {

    private final CodeMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptCdGroupRepo cmptCdGroupRepo;
    private final CmptCdRepo cmptCdRepo;
    private final CmptRfrncCdRepo cmptRfrncCdRepo;
    // 언어는 데이터다 — 언어 코드를 리터럴로 박지 않는다. 판정은 LocaleSupport 한 곳에서 한다.
    private final LocaleSupport localeSupport;

    /**
     * 시스템 대분류 코드 조회
     * @param dto 대분류 코드 요청
     * @return 대분류 코드 응답 목록
     */
    @Transactional(readOnly = true)
    public List<SYS5040002Dto> selectSysLCatCdList(SYS5040001Dto dto) {
        SYS5040001Vo vo = modelConveter.map(dto, SYS5040001Vo.class);
        vo.setLocale(localeSupport.current());
        return modelConveter.mapList(mapper.selectSysLCatCdList(vo), SYS5040002Dto.class);
    }

    /**
     * 시스템 대분류 코드 상세 조회
     * @param dto 대분류 코드 요청
     * @return 대분류 코드 응답
     */
    @Transactional(readOnly = true)
    public SYS5040002Dto selectSysLCatCdDtl(SYS5040001Dto dto) {
        SYS5040001Vo vo = modelConveter.map(dto, SYS5040001Vo.class);
        vo.setLocale(localeSupport.current());
        return modelConveter.map(mapper.selectSysLCatCdDtl(vo), SYS5040002Dto.class);
    }

    /**
     * 시스템 소분류 코드 조회
     * @param dto 소분류 코드 요청
     * @return 소분류 코드 응답 목록
     */
    @Transactional(readOnly = true)
    public List<SYS5040004Dto> selectSysSCatCdList(SYS5040003Dto dto) {
        SYS5040003Vo vo = modelConveter.map(dto, SYS5040003Vo.class);
        return modelConveter.mapList(mapper.selectSysSCatCdList(vo), SYS5040004Dto.class);
    }

    /**
     * 시스템 소분류 코드 상세 조회
     * @param dto 소분류 코드 요청
     * @return 소분류 코드 응답
     */
    @Transactional(readOnly = true)
    public SYS5040004Dto selectSysSCatCdDtl(SYS5040003Dto dto) {
        SYS5040003Vo vo = modelConveter.map(dto, SYS5040003Vo.class);
        return modelConveter.map(mapper.selectSysSCatCdDtl(vo), SYS5040004Dto.class);
    }

    /**
     * 시스템 소분류 코드 참고 정보 조회
     * @param dto 소분류 코드 요청
     * @return 소분류 코드 참고 응답 목록
     */
    @Transactional(readOnly = true)
    public List<SYS5040005Dto> selectSysSCatCdRefList(SYS5040003Dto dto) {
        SYS5040003Vo vo = modelConveter.map(dto, SYS5040003Vo.class);
        return modelConveter.mapList(mapper.selectSysSCatCdRefList(vo), SYS5040005Dto.class);
    }

    /**
     * 시스템 대분류 코드 저장
     * @param dto 대분류 코드 응답
     * @return 대분류 코드 응답
     */
    public Integer updateSysLCatCd(SYS5040002Dto dto) {

        if (ObjectUtils.isEmpty(dto.getCdGroup())) {
            throw new InvalidationException("코드 그룹을 입력하세요.");
        }

        Optional<CmptCdGroupEntity> existingEntity = cmptCdGroupRepo.findById(dto.getCdGroup());
        CmptCdGroupEntity entity;

        if (existingEntity.isPresent()) {
            entity = existingEntity.get();
            BeanUtils.copyProperties(dto, entity, "cdGroup");
        } else {
            entity = new CmptCdGroupEntity();
            BeanUtils.copyProperties(dto, entity);
        }
        cmptCdGroupRepo.save(entity);

        return 1;
    }

    /**
     * 시스템 소분류 코드 저장
     * @param dto 소분류 코드 응답
     * @return 처리 건수
     */
    public Integer updateSysSCatCd(SYS5040004Dto dto) {

        if (ObjectUtils.isEmpty(dto.getCdGroup())) {
            throw new InvalidationException("코드 그룹은 필수입니다.");
        }
        if (ObjectUtils.isEmpty(dto.getCd())) {
            throw new InvalidationException("코드는 필수입니다.");
        }

        String locale = resolveLocale(dto.getLocale());
        CmptCdPkey pkey = new CmptCdPkey(dto.getCd(), dto.getCdGroup(), locale);
        Optional<CmptCdEntity> existingEntityOpt = cmptCdRepo.findById(pkey);

        CmptCdEntity entity;
        if (existingEntityOpt.isPresent()) {
            entity = existingEntityOpt.get();
            BeanUtils.copyProperties(dto, entity, "cdGroup", "cd", "locale");
        } else {
            entity = new CmptCdEntity();
            entity.setId(pkey);
            BeanUtils.copyProperties(dto, entity);
        }
        cmptCdRepo.save(entity);
        
        return 1;
    }
    
    /**
     * 시스템 소분류 코드 참고 정보 저장
     * @param dtoList 소분류 코드 참고 응답 목록
     * @return 처리 건수
     */
    public Integer updateSysSCatCdRefList(List<SYS5040005Dto> dtoList) {

        String cdGroup = dtoList.get(0).getCdGroup();
        String cd = dtoList.get(0).getCd();
        String locale = resolveLocale(dtoList.get(0).getLocale());
        
        // 기존 데이터 삭제 — 선택한 로케일만 지운다
        cmptRfrncCdRepo.deleteById(cdGroup, cd, locale);

        for (SYS5040005Dto dto : dtoList) {

            if (ObjectUtils.isEmpty(dto.getRfrncCd())) {
                continue;
            }

            if (ObjectUtils.isEmpty(dto.getCdGroup())) {
                throw new InvalidationException("코드 그룹은 필수입니다.");
            }
            if (ObjectUtils.isEmpty(dto.getCd())) {
                throw new InvalidationException("코드는 필수입니다.");
            }

            CmptRfrncCdPkey pkey = new CmptRfrncCdPkey(dto.getCd(), dto.getCdGroup(), locale, dto.getRfrncCd());
            CmptRfrncCdEntity entity = new CmptRfrncCdEntity();
            entity.setId(pkey);
            BeanUtils.copyProperties(dto, entity);

            cmptRfrncCdRepo.save(entity);
        }
        return dtoList.size();
    }

    /** 화면에서 고른 로케일이 있으면 그 값, 없으면 요청 로케일. */
    private String resolveLocale(String locale) {
        return ObjectUtils.isEmpty(locale) ? localeSupport.current() : locale;
    }
} 