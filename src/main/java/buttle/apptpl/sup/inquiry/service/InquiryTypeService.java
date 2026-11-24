package buttle.apptpl.sup.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.exception.InvalidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import buttle.apptpl.api.shared.dto.*;
import buttle.apptpl.sup.inquiry.mapper.InquiryTypeMapper;
import buttle.apptpl.api.shared.vo.*;
import buttle.apptpl.common.util.ModelConveter;

import java.util.List;
import java.util.UUID;

/**
 * BO303_P1 문의 유형 관리 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryTypeService {

    private final InquiryTypeMapper mapper;
    private final ModelConveter modelConveter;

    /**
     * 문의 유형 목록 조회
     *
     * @param dto 검색 조건 DTO
     * @return 문의 유형 목록
     */
    @Transactional(readOnly = true)
    public List<InquiryTypeListResDto> getInquiryTypeList(InquiryTypeSearchReqDto dto) {
        log.info("===== 문의 유형 목록 조회 시작 =====");
        log.debug("검색 조건: {}", dto);

        // DTO -> VO 변환
        InquiryTypeSearchReqVo vo = modelConveter.map(dto, InquiryTypeSearchReqVo.class);

        // 목록 조회
        List<InquiryTypeListResVo> resultList = mapper.selectInquiryTypeList(vo);
        log.info("조회 결과 건수: {}", resultList.size());

        // VO -> DTO 변환
        List<InquiryTypeListResDto> dtoList = modelConveter.mapList(resultList, InquiryTypeListResDto.class);

        log.info("===== 문의 유형 목록 조회 종료 =====");
        return dtoList;
    }

    /**
     * 문의 유형 등록
     *
     * @param dto 등록 요청 DTO
     * @return 등록 결과 DTO
     */
    @Transactional
    public InquiryTypeSaveResDto createInquiryType(InquiryTypeSaveReqDto dto) {
        log.info("===== 문의 유형 등록 시작 =====");
        log.debug("등록 데이터: {}", dto);

        // 1. 코드 자동 생성 (UUID 기반 3자리)
        String generatedCd = generateCode();
        log.debug("생성된 코드: {}", generatedCd);

        // 2. 정렬 순서 자동 설정
        int nextSortSn = mapper.getNextSortSn();
        log.debug("정렬 순서: {}", nextSortSn);

        // 3. 현재 사용자 정보 가져오기
        String userId = getCurrentUserId();
        log.debug("등록자 ID: {}", userId);

        // 4. DTO -> VO 변환
        InquiryTypeSaveReqVo vo = InquiryTypeSaveReqVo.builder()
                .cd(generatedCd)
                .cdNm(dto.getCdNm())
                .sortSn(nextSortSn)
                .rgtrId(userId)
                .mdfrId(userId)
                .build();

        // 5. 등록 실행
        int result = mapper.insertInquiryType(vo);
        log.info("등록 결과: {}", result);

        if (result <= 0) {
            throw new RuntimeException("문의 유형 등록에 실패했습니다.");
        }

        // 6. 응답 DTO 생성
        InquiryTypeSaveResDto responseDto = InquiryTypeSaveResDto.builder()
                .cd(generatedCd)
                .message("문의 유형이 등록되었습니다.")
                .build();

        log.info("===== 문의 유형 등록 종료 =====");
        return responseDto;
    }

    /**
     * 문의 유형 수정
     *
     * @param dto 수정 요청 DTO
     * @return 수정 결과 DTO
     */
    @Transactional
    public InquiryTypeSaveResDto updateInquiryType(InquiryTypeSaveReqDto dto) {
        log.info("===== 문의 유형 수정 시작 =====");
        log.debug("수정 데이터: {}", dto);

        // 1. 현재 사용자 정보 가져오기
        String userId = getCurrentUserId();
        log.debug("수정자 ID: {}", userId);

        // 2. DTO -> VO 변환
        InquiryTypeSaveReqVo vo = InquiryTypeSaveReqVo.builder()
                .cd(dto.getCd())
                .cdNm(dto.getCdNm())
                .mdfrId(userId)
                .build();

        // 3. 수정 실행
        int result = mapper.updateInquiryType(vo);
        log.info("수정 결과: {}", result);

        if (result <= 0) {
            throw new RuntimeException("문의 유형 수정에 실패했습니다.");
        }

        // 4. 응답 DTO 생성
        InquiryTypeSaveResDto responseDto = InquiryTypeSaveResDto.builder()
                .cd(dto.getCd())
                .message("문의 유형이 수정되었습니다.")
                .build();

        log.info("===== 문의 유형 수정 종료 =====");
        return responseDto;
    }

    /**
     * 문의 유형 사용 여부 확인
     *
     * @param cd 코드
     * @return 사용 건수
     */
    @Transactional(readOnly = true)
    public Integer checkInquiryTypeInUse(String cd) {
        log.info("===== 문의 유형 사용 여부 확인 시작 =====");
        log.debug("확인 코드: {}", cd);

        int inUseCount = mapper.checkInquiryTypeInUse(cd);
        log.debug("사용 건수: {}", inUseCount);

        log.info("===== 문의 유형 사용 여부 확인 종료 =====");
        return inUseCount;
    }

    /**
     * 문의 유형 삭제
     *
     * @param dto 삭제 요청 DTO
     */
    @Transactional
    public void deleteInquiryType(InquiryTypeDeleteReqDto dto) {
        log.info("===== 문의 유형 삭제 시작 =====");
        log.debug("삭제 코드: {}", dto.getCd());

        // 1. 현재 사용자 정보 가져오기
        String userId = getCurrentUserId();
        log.debug("수정자 ID: {}", userId);

        // 2. DTO -> VO 변환
        InquiryTypeDeleteReqVo vo = InquiryTypeDeleteReqVo.builder()
                .cd(dto.getCd())
                .mdfrId(userId)
                .build();

        // 3. 삭제 실행 (논리 삭제)
        int result = mapper.deleteInquiryType(vo);
        log.info("삭제 결과: {}", result);

        if (result <= 0) {
            throw new RuntimeException("문의 유형 삭제에 실패했습니다.");
        }

        log.info("===== 문의 유형 삭제 종료 =====");
    }

    /**
     * 현재 로그인 사용자 ID 조회
     *
     * @return 사용자 ID
     */
    private String getCurrentUserId() {
        SecUser secUser = SecurityUtils.getCurrentUser();
        if (secUser == null || !secUser.getEnabled()) {
            throw new InvalidationException("사용자 정보가 없습니다.");
        }
        return secUser.getUserId();
    }

    /**
     * 코드 자동 생성 (3자리 대문자)
     *
     * @return 생성된 코드
     */
    private String generateCode() {
        // UUID 기반으로 3자리 대문자 코드 생성
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return uuid.substring(0, 3);
    }
}
