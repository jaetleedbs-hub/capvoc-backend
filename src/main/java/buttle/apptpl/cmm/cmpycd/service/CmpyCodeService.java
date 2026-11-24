package buttle.apptpl.cmm.cmpycd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.api.shared.dto.*;
import buttle.apptpl.cmm.cmpycd.mapper.CmpyCodeMapper;
import buttle.apptpl.api.shared.vo.*;
import micoai.corebase.exception.InvalidationException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BO403_S 회원사 공통 코드 관리 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CmpyCodeService {

    private final CmpyCodeMapper mapper;

    /**
     * 코드 그룹 목록조회
     */
    public List<CodeGroupListResDto> getCodeGroupList(CodeGroupSearchReqDto dto) {
        log.debug("코드 그룹 목록조회 시작 - cmpyTypeCd: {}", dto.getCmpyTypeCd());

        if (ObjectUtils.isEmpty(dto.getCmpyTypeCd())) {
            throw new InvalidationException("회원사유형을 선택해 주세요.");
        }

        try {
            BO403SCodeGroupSearchReqVo vo = new BO403SCodeGroupSearchReqVo();
            BeanUtils.copyProperties(dto, vo);

            List<BO403SCodeGroupListResVo> voList = mapper.selectCodeGroupList(vo);

            return voList.stream().map(v -> {
                CodeGroupListResDto result = new CodeGroupListResDto();
                BeanUtils.copyProperties(v, result);
                return result;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("코드 그룹 목록조회 실패", e);
            throw new InvalidationException("코드 그룹 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 코드 목록조회
     */
    public List<CodeListResDto> getCodeList(CodeSearchReqDto dto) {
        log.debug("코드 목록조회 시작 - cmpyTypeCd: {}, cdGroup: {}", dto.getCmpyTypeCd(), dto.getCdGroup());

        if (ObjectUtils.isEmpty(dto.getCmpyTypeCd())) {
            throw new InvalidationException("회원사유형을 선택해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getCdGroup())) {
            throw new InvalidationException("코드그룹을 선택해 주세요.");
        }

        try {
            BO403SCodeSearchReqVo vo = new BO403SCodeSearchReqVo();
            BeanUtils.copyProperties(dto, vo);

            List<BO403SCodeListResVo> voList = mapper.selectCodeList(vo);

            return voList.stream().map(v -> {
                CodeListResDto result = new CodeListResDto();
                BeanUtils.copyProperties(v, result);
                return result;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("코드 목록조회 실패", e);
            throw new InvalidationException("코드 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 코드 그룹 저장
     */
    public CodeCommonSaveResDto saveCodeGroup(CodeGroupSaveReqDto dto) {
        log.debug("코드 그룹 저장 시작 - cmpyTypeCd: {}, cdGroup: {}", dto.getCmpyTypeCd(), dto.getCdGroup());

        // 유효성 검증
        if (ObjectUtils.isEmpty(dto.getCmpyTypeCd())) {
            throw new InvalidationException("회원사유형을 선택해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getCdGroup())) {
            throw new InvalidationException("코드그룹을 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getCdGroupNm())) {
            throw new InvalidationException("코드그룹명을 입력해 주세요.");
        }

        try {
            BO403SCodeGroupSaveReqVo vo = new BO403SCodeGroupSaveReqVo();
            BeanUtils.copyProperties(dto, vo);

            // 존재 여부 확인
            int exists = mapper.selectCodeGroupExists(vo);
            int result;

            if (exists > 0) {
                // 수정
                result = mapper.updateCodeGroup(vo);
            } else {
                // 등록
                result = mapper.insertCodeGroup(vo);
            }

            log.debug("코드 그룹 저장 완료 - cmpyTypeCd: {}, cdGroup: {}", dto.getCmpyTypeCd(), dto.getCdGroup());

            return CodeCommonSaveResDto.builder()
                    .successCount(result)
                    .message("저장 완료되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("코드 그룹 저장 실패", e);
            throw new InvalidationException("코드 그룹 저장에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 코드 저장
     */
    public CodeCommonSaveResDto saveCode(CodeSaveReqDto dto) {
        log.debug("코드 저장 시작 - cmpyTypeCd: {}, cdGroup: {}, cd: {}", 
                dto.getCmpyTypeCd(), dto.getCdGroup(), dto.getCd());

        // 유효성 검증
        if (ObjectUtils.isEmpty(dto.getCmpyTypeCd())) {
            throw new InvalidationException("회원사유형을 선택해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getCdGroup())) {
            throw new InvalidationException("코드그룹을 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getCd())) {
            throw new InvalidationException("코드를 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getLocale())) {
            throw new InvalidationException("로케일을 선택해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getCdNm())) {
            throw new InvalidationException("코드명을 입력해 주세요.");
        }

        try {
            BO403SCodeSaveReqVo vo = new BO403SCodeSaveReqVo();
            BeanUtils.copyProperties(dto, vo);

            // 존재 여부 확인
            int exists = mapper.selectCodeExists(vo);
            int result;

            if (exists > 0) {
                // 수정
                result = mapper.updateCode(vo);
            } else {
                // 등록
                result = mapper.insertCode(vo);
            }

            log.debug("코드 저장 완료 - cmpyTypeCd: {}, cdGroup: {}, cd: {}", 
                    dto.getCmpyTypeCd(), dto.getCdGroup(), dto.getCd());

            return CodeCommonSaveResDto.builder()
                    .successCount(result)
                    .message("저장 완료되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("코드 저장 실패", e);
            throw new InvalidationException("코드 저장에 실패했습니다: " + e.getMessage());
        }
    }
}
