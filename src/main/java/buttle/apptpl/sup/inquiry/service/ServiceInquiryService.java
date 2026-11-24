package buttle.apptpl.sup.inquiry.service;

import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.api.shared.dto.*;
import buttle.apptpl.sup.inquiry.mapper.ServiceInquiryMapper;
import buttle.apptpl.api.shared.vo.*;
import buttle.apptpl.common.util.ModelConveter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * BO303_S 서비스 문의 관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceInquiryService {
    
    private final ServiceInquiryMapper mapper;
    private final ModelConveter modelConveter;
    
    /**
     * 서비스 문의 목록 조회
     * @param dto 서비스 문의 검색 요청
     * @return 서비스 문의 목록
     */
    @Transactional(readOnly = true)
    public List<InquiryListResDto> getInquiryList(InquirySearchReqDto dto) {
        // 기간 검증
        if (!ObjectUtils.isEmpty(dto.getFromDt()) && !ObjectUtils.isEmpty(dto.getToDt())) {
            if (dto.getToDt().compareTo(dto.getFromDt()) < 0) {
                throw new InvalidationException("시작일은 종료일보다 이전이어야 합니다.");
            }
        }
        
        InquirySearchReqVo vo = modelConveter.map(dto, InquirySearchReqVo.class);
        List<InquiryListResVo> result = mapper.selectInquiryList(vo);
        return modelConveter.mapList(result, InquiryListResDto.class);
    }
    
    /**
     * 서비스 문의 상세 조회
     * @param dto 서비스 문의 상세 요청
     * @return 서비스 문의 상세 정보
     
    @Transactional(readOnly = true)
    public InquiryDetailResDto getInquiryDetail(InquiryDetailReqDto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getQstnId())) {
            throw new InvalidationException("문의ID를 입력해 주세요.");
        }
        
        InquiryDetailReqVo vo = modelConveter.map(dto, InquiryDetailReqVo.class);
        InquiryDetailResVo result = mapper.selectInquiryDetail(vo);
        
        if (result == null) {
            throw new InvalidationException("서비스 문의 정보를 찾을 수 없습니다.");
        }
        
        return modelConveter.map(result, InquiryDetailResDto.class);
    }
    */
    /**
     * 서비스 문의 답변 저장
     * @param dto 서비스 문의 답변 저장 요청
     * @return 서비스 문의 답변 저장 응답
     */
    public InquiryAnswerSaveResDto saveInquiryAnswer(InquiryAnswerSaveReqDto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getQstnId())) {
            throw new InvalidationException("문의ID를 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getAnswerCn())) {
            throw new InvalidationException("답변 내용을 입력해 주세요.");
        }
        
        // 답변 내용 길이 검증
        if (dto.getAnswerCn().length() > 1000) {
            throw new InvalidationException("답변 내용은 최대 1000자까지 입력 가능합니다.");
        }
        
        InquiryAnswerSaveReqVo vo = modelConveter.map(dto, InquiryAnswerSaveReqVo.class);
        int result = mapper.updateInquiryAnswer(vo);
        
        if (result <= 0) {
            throw new InvalidationException("서비스 문의 답변 저장에 실패했습니다.");
        }
        
        // 응답 생성
        return InquiryAnswerSaveResDto.builder()
                .qstnId(dto.getQstnId())
                .success(true)
                .build();
    }
    
    /**
     * 서비스 문의 메일 발송
     * @param dto 서비스 문의 메일 발송 요청
     * @return 서비스 문의 메일 발송 응답
     */
    public InquiryMailSendResDto sendInquiryMail(InquiryMailSendReqDto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getQstnId())) {
            throw new InvalidationException("문의ID를 입력해 주세요.");
        }
        
        // TODO: 메일 발송 로직 구현
        // 실제 메일 발송 서비스 연동 필요
        
        // 응답 생성
        return InquiryMailSendResDto.builder()
                .success(true)
                .message("답변 메일이 성공적으로 발송되었습니다.")
                .build();
    }
    
    /**
     * 서비스 문의 등록
     *
     * <p>상담사 콘솔(desk-backend)의 서버 간 호출 전용이다.
     * 문의는 admin 스키마의 cmpt_srvc_qstn 에만 존재하므로 desk 가 직접 쓸 수 없다.</p>
     *
     * @param dto 서비스 문의 등록 요청
     * @return 생성된 문의 ID
     */
    public InquiryRegisterResDto registerInquiry(InquiryRegisterReqDto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getCompanyId())) {
            throw new InvalidationException("회원사 ID를 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getSrvcQstnClsfCd())) {
            throw new InvalidationException("문의 유형을 선택해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getSrvcQstnCn())) {
            throw new InvalidationException("문의 내용을 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getRgtrId())) {
            throw new InvalidationException("등록자 정보를 확인할 수 없습니다.");
        }

        InquiryRegisterReqVo vo = modelConveter.map(dto, InquiryRegisterReqVo.class);
        vo.setSrvcQstnId(generateInquiryId());
        if (ObjectUtils.isEmpty(vo.getMdfrId())) {
            vo.setMdfrId(vo.getRgtrId());
        }

        int result = mapper.insertInquiry(vo);
        if (result <= 0) {
            throw new InvalidationException("서비스 문의 등록에 실패했습니다.");
        }

        return InquiryRegisterResDto.builder()
                .qstnId(vo.getSrvcQstnId())
                .success(true)
                .build();
    }

    /**
     * 내 서비스 문의 목록 조회 (등록자 본인 것만)
     *
     * @param dto 검색 조건
     * @param pageable 페이징 정보
     * @return 서비스 문의 목록
     */
    @Transactional(readOnly = true)
    public Page<MyInquiryListResDto> getMyInquiryList(MyInquirySearchReqDto dto, Pageable pageable) {
        if (ObjectUtils.isEmpty(dto.getCompanyId())) {
            throw new InvalidationException("회원사 ID를 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getLoginId())) {
            throw new InvalidationException("조회 대상 사용자 정보를 확인할 수 없습니다.");
        }

        MyInquirySearchReqVo vo = modelConveter.map(dto, MyInquirySearchReqVo.class);
        Page<MyInquiryListResVo> result = mapper.selectMyInquiryList(vo, pageable);
        return result.map(item -> modelConveter.map(item, MyInquiryListResDto.class));
    }

    /**
     * 서비스 문의 ID 채번
     *
     * <p>목록/커서 조회가 ID 순서에 의존하므로 시간순으로 증가하는 값을 만든다.</p>
     */
    private String generateInquiryId() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return "SQ" + ts + suffix;
    }

    /**
     * 문의 유형 목록 조회
     * @return 문의 유형 목록
     */
    @Transactional(readOnly = true)
    public List<InquiryTypeResDto> getInquiryTypeList() {
        List<InquiryTypeResVo> result = mapper.selectInquiryTypeList();
        return modelConveter.mapList(result, InquiryTypeResDto.class);
    }
}
