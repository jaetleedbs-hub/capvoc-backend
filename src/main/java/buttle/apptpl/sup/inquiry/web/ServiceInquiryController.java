package buttle.apptpl.sup.inquiry.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.api.shared.dto.*;
import buttle.apptpl.sup.inquiry.service.ServiceInquiryService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * BO303_S 서비스 문의 관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sup/inquiry")
@Tag(name = "ServiceInquiryController", description = "서비스 문의 관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class ServiceInquiryController {
    
    private final ServiceInquiryService service;
    
    /**
     * 서비스 문의 목록 조회
     */
    @PostMapping("/inquiry")
    @Operation(summary = "서비스 문의 목록 조회", description = "검색 조건에 따라 서비스 문의 목록을 조회합니다")
    public List<InquiryListResDto> getInquiryList(@RequestBody InquirySearchReqDto dto) {
        return service.getInquiryList(dto);
    }
    
    /**
     * 서비스 문의 상세 조회
    @GetMapping("/inquiry/{id}")
    @Operation(summary = "서비스 문의 상세 조회", description = "문의ID로 서비스 문의 상세 정보를 조회합니다")
    public InquiryDetailResDto getInquiryDetail(@PathVariable("id") String id) {
        InquiryDetailReqDto dto = InquiryDetailReqDto.builder()
                .qstnId(id)
                .build();
        return service.getInquiryDetail(dto);
    }
     */
    /**
     * 서비스 문의 답변 저장
     */
    @PostMapping("/inquiry/answer")
    @Operation(summary = "서비스 문의 답변 저장", description = "서비스 문의에 대한 답변을 저장합니다")
    public InquiryAnswerSaveResDto saveInquiryAnswer(@RequestBody InquiryAnswerSaveReqDto dto) {
        return service.saveInquiryAnswer(dto);
    }
    
    /**
     * 서비스 문의 메일 발송
     */
    @PostMapping("/inquiry/send-mail")
    @Operation(summary = "서비스 문의 메일 발송", description = "답변 완료된 서비스 문의에 대해 메일을 발송합니다")
    public InquiryMailSendResDto sendInquiryMail(@RequestBody InquiryMailSendReqDto dto) {
        return service.sendInquiryMail(dto);
    }
    
    /*
     * 첨부파일 목록 조회는 공통 파일 API(GET /api/common/files/{groupId})로 일원화한다.
     * 화면(BO303_S FileManager)도 그쪽을 쓰므로 여기 있던 /inquiry/files 는 삭제했다.
     */

    /**
     * 서비스 문의 등록 (상담사 콘솔 → admin, 서버 간 호출)
     */
    @PostMapping("/register")
    @Operation(summary = "서비스 문의 등록", description = "상담사 콘솔에서 접수한 서비스 문의를 저장합니다")
    public InquiryRegisterResDto registerInquiry(@RequestBody InquiryRegisterReqDto dto) {
        return service.registerInquiry(dto);
    }

    /**
     * 내 서비스 문의 목록 조회 (상담사 콘솔 → admin, 서버 간 호출)
     */
    @PostMapping("/my-list")
    @Operation(summary = "내 서비스 문의 목록 조회", description = "로그인 사용자가 등록한 서비스 문의 목록을 조회합니다")
    public Page<MyInquiryListResDto> getMyInquiryList(@RequestBody MyInquirySearchReqDto dto,
                                                     @ParameterObject Pageable pageable) {
        return service.getMyInquiryList(dto, pageable);
    }

    /**
     * 문의 유형 목록 조회
     */
    @GetMapping("/inquiry-types")
    @Operation(summary = "문의 유형 목록 조회", description = "문의 유형 코드 목록을 조회합니다")
    public List<InquiryTypeResDto> getInquiryTypeList() {
        return service.getInquiryTypeList();
    }
}
