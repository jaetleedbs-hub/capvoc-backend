package buttle.apptpl.board.faq.service;

import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.api.shared.dto.*;
import buttle.apptpl.board.faq.mapper.FaqMapper;
import buttle.apptpl.api.shared.vo.FaqCategoryResVo;
import buttle.apptpl.api.shared.vo.FaqDeleteReqVo;
import buttle.apptpl.api.shared.vo.FaqDetailReqVo;
import buttle.apptpl.api.shared.vo.FaqDetailResVo;
import buttle.apptpl.api.shared.vo.FaqListResVo;
import buttle.apptpl.api.shared.vo.FaqSearchReqVo;
import buttle.apptpl.common.model.cmpt.entity.CmptBbsPstEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptBbsRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptBbsPstRepo;
import buttle.apptpl.common.util.ModelConveter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * BO302_S 시스템 FAQ 관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FaqService {

    private final FaqMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptBbsRepo cmptBbsRepo;
    private final CmptBbsPstRepo cmptBbsPstRepo;

    /**
     * FAQ 목록조회
     * @param dto FAQ 검색 요청
     * @return FAQ 목록
     */
    public Page<FaqListResDto> selectFaqList(FaqSearchReqDto dto, Pageable pageable) {
        
        FaqSearchReqVo vo = modelConveter.map(dto, FaqSearchReqVo.class);
        Page<FaqListResVo> result = mapper.selectFaqList(vo, pageable);
        return result.map(voItem -> modelConveter.map(voItem, FaqListResDto.class));
    }

    /**
     * FAQ 상세조회
     * @param dto FAQ 상세 요청
     * @return FAQ 상세 응답
     */
    public FaqDetailResDto selectFaqDetail(FaqDetailReqDto dto) {
        FaqDetailReqVo vo = modelConveter.map(dto, FaqDetailReqVo.class);
        FaqDetailResVo result = mapper.selectFaqDetail(vo);
        return modelConveter.map(result, FaqDetailResDto.class);
    }

    /**
     * FAQ 분류 목록 조회
     * @return FAQ 분류 목록
     */
    public List<FaqCategoryResDto> selectFaqCategory() {
        List<FaqCategoryResVo> result = mapper.selectFaqCategory();
        return modelConveter.mapList(result, FaqCategoryResDto.class);
    }

    /**
     * FAQ 총 건수 조회
     * @return FAQ 총 건수
     */
    public int selectFaqListCount(FaqSearchReqDto dto) {
        return mapper.selectFaqListCount(modelConveter.map(dto, FaqSearchReqVo.class));
    }

    /**
     * FAQ 저장
     * @param dto FAQ 저장 요청
     * @return FAQ 저장 응답
     */
    public FaqSaveResDto saveFaq(FaqSaveReqDto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getCategoryCd())) {
            throw new InvalidationException("분류를 선택하세요.");
        }
        if (ObjectUtils.isEmpty(dto.getQuestion())) {
            throw new InvalidationException("질문을 입력하세요.");
        }
        if (ObjectUtils.isEmpty(dto.getAnswer())) {
            throw new InvalidationException("답변을 입력하세요.");
        }
        if (ObjectUtils.isEmpty(dto.getDisplayYn())) {
            throw new InvalidationException("노출 여부를 선택하세요.");
        }

        // 게시판 존재 확인
        String bbsId = ObjectUtils.isEmpty(dto.getBbsId()) ? "FAQ" : dto.getBbsId();
        if (!cmptBbsRepo.existsById(bbsId)) {
            throw new InvalidationException("게시판 정보가 존재하지 않습니다.");
        }

        CmptBbsPstEntity entity;
        String pstId;

        // 신규 등록 vs 수정 분기
        if (ObjectUtils.isEmpty(dto.getPstId())) {
            // 신규 등록
            pstId = UUID.randomUUID().toString();
            entity = new CmptBbsPstEntity();
            entity.setId(pstId);
            entity.setBbsId(bbsId);
        } else {
            // 수정
            pstId = dto.getPstId();
            Optional<CmptBbsPstEntity> existingEntityOpt = cmptBbsPstRepo.findById(pstId);
            if (!existingEntityOpt.isPresent()) {
                throw new InvalidationException("FAQ 정보가 존재하지 않습니다.");
            }
            entity = existingEntityOpt.get();
        }

        // DTO → Entity 매핑
        entity.setBbsTypeCd(dto.getCategoryCd());
        entity.setTtl(dto.getQuestion());
        entity.setPstCn(dto.getAnswer());
        entity.setNtcYn(dto.getDisplayYn());
        entity.setUseYn("Y");

        // 저장
        cmptBbsPstRepo.save(entity);

        // 응답 생성
        FaqSaveResDto result = new FaqSaveResDto();
        result.setBbsId(bbsId);
        result.setPstId(pstId);
        return result;
    }

    /**
     * FAQ 삭제
     * @param dto FAQ 삭제 요청
     * @return 처리 건수
     */
    public Integer deleteFaq(List<FaqDeleteReqDto> list) {
        // 필수 필드 검증

        for (FaqDeleteReqDto dto : list) {

            if (ObjectUtils.isEmpty(dto.getPstId())) {
                throw new InvalidationException("삭제할 FAQ를 선택하세요.");
            }

            Optional<CmptBbsPstEntity> entityOpt = cmptBbsPstRepo.findById(dto.getPstId());
            if (!entityOpt.isPresent()) {
                throw new InvalidationException("FAQ 정보가 존재하지 않습니다.");
            }
        }
        // 삭제
        return  mapper.deleteFaqList(list.stream()
        .map(dto -> modelConveter.map(dto, FaqDeleteReqVo.class))
        .collect(Collectors.toList()));
    }
}
