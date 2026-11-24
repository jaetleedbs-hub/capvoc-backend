package buttle.apptpl.sup.inquiry.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.api.shared.dto.*;
import buttle.apptpl.sup.inquiry.service.InquiryTypeService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * BO303_P1 문의 유형 관리 Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sup/inquiry")
@Tag(name = "InquiryTypeController", description = "문의 유형 관리")
@Validated
public class InquiryTypeController {

    private final InquiryTypeService service;

    /**
     * 문의 유형 목록 조회
     *
     * @param dto 검색 조건 DTO
     * @return 문의 유형 목록
     */
    @PostMapping("/inquiry/type")
    @Operation(summary = "문의 유형 목록 조회", description = "검색 조건에 따라 문의 유형 목록을 조회합니다")
    public List<InquiryTypeListResDto> getInquiryTypeList(
            @RequestBody InquiryTypeSearchReqDto dto) {
        log.info("===== [BO303_P1 R01] 문의 유형 목록 조회 API 호출 =====");
        List<InquiryTypeListResDto> resultList = service.getInquiryTypeList(dto);
        log.info("===== [BO303_P1 R01] 문의 유형 목록 조회 API 종료 =====");
        return resultList;
    }

    /**
     * 문의 유형 등록
     *
     * @param dto 등록 요청 DTO
     * @return 등록 결과 DTO
     */
    @PostMapping("/inquiry/type/create")
    @Operation(summary = "문의 유형 등록", description = "신규 문의 유형을 등록합니다")
    public InquiryTypeSaveResDto createInquiryType(
            @Valid @RequestBody InquiryTypeSaveReqDto dto) {
        log.info("===== [BO303_P1 C01] 문의 유형 등록 API 호출 =====");
        InquiryTypeSaveResDto result = service.createInquiryType(dto);
        log.info("===== [BO303_P1 C01] 문의 유형 등록 API 종료 =====");
        return result;
    }

    /**
     * 문의 유형 사용 여부 확인
     *
     * @param dto 확인 요청 DTO (cd 포함)
     * @return 사용 건수
     */
    @PostMapping("/inquiry/type/check-in-use")
    @Operation(summary = "문의 유형 사용 여부 확인", description = "문의 유형이 실제 문의에서 사용되고 있는지 확인합니다")
    public Integer checkInquiryTypeInUse(@RequestBody InquiryTypeDeleteReqDto dto) {
        log.info("===== [BO303_P1] 문의 유형 사용 여부 확인 API 호출 =====");
        log.debug("확인 코드: {}", dto.getCd());
        
        Integer count = service.checkInquiryTypeInUse(dto.getCd());
        
        log.info("===== [BO303_P1] 문의 유형 사용 여부 확인 API 종료 - 사용 건수: {} =====", count);
        return count;
    }

    /**
     * 문의 유형 수정
     *
     * @param dto 수정 요청 DTO
     * @return 수정 결과 DTO
     */
    @PostMapping("/inquiry/type/u01")
    @Operation(summary = "문의 유형 수정", description = "선택한 문의 유형을 수정합니다")
    public InquiryTypeSaveResDto updateInquiryType(
            @Valid @RequestBody InquiryTypeSaveReqDto dto) {
        log.info("===== [BO303_P1 U01] 문의 유형 수정 API 호출 =====");
        log.debug("수정 데이터: {}", dto);
        
        InquiryTypeSaveResDto result = service.updateInquiryType(dto);
        log.info("===== [BO303_P1 U01] 문의 유형 수정 API 종료 =====");
        return result;
    }

    /**
     * 문의 유형 삭제
     *
     * @param dto 삭제 요청 DTO
     * @return 처리 결과 메시지
     */
    @PostMapping("/inquiry/type/d01")
    @Operation(summary = "문의 유형 삭제", description = "선택한 문의 유형을 삭제합니다")
    public String deleteInquiryType(
            @Valid @RequestBody InquiryTypeDeleteReqDto dto) {
        log.info("===== [BO303_P1 D01] 문의 유형 삭제 API 호출 =====");
        log.debug("삭제 코드: {}", dto.getCd());

        service.deleteInquiryType(dto);

        log.info("===== [BO303_P1 D01] 문의 유형 삭제 API 종료 =====");
        return "문의 유형이 삭제되었습니다.";
    }
}
