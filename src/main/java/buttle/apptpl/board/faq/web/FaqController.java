package buttle.apptpl.board.faq.web;

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
import buttle.apptpl.board.faq.service.FaqService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * BO302_S 시스템 FAQ 관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board/faq")
@Tag(name = "FaqController", description = "시스템 FAQ 관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class FaqController {

    private final FaqService service;

    /**
     * FAQ 목록조회
     */
    @PostMapping("/r01")
    @Operation(summary = "FAQ 목록조회", description = "FAQ 목록조회")
    public Page<FaqListResDto> selectFaqList(@RequestBody FaqSearchReqDto dto, @ParameterObject Pageable pageable) {
        return service.selectFaqList(dto, pageable);
    }

    /**
     * FAQ 상세조회
     */
    @PostMapping("/r02")
    @Operation(summary = "FAQ 상세조회", description = "FAQ 상세조회")
    public FaqDetailResDto selectFaqDetail(@RequestBody FaqDetailReqDto dto) {
        return service.selectFaqDetail(dto);
    }

    /**
     * FAQ 분류 목록 조회
     */
    @PostMapping("/r03")
    @Operation(summary = "FAQ 분류 목록 조회", description = "FAQ 분류 코드 목록 조회")
    public List<FaqCategoryResDto> selectFaqCategory() {
        return service.selectFaqCategory();
    }

    /**
     * FAQ 저장
     */
    @PostMapping("/u01")
    @Operation(summary = "FAQ 저장", description = "FAQ 저장")
    public FaqSaveResDto saveFaq(@RequestBody FaqSaveReqDto dto) {
        return service.saveFaq(dto);
    }

    /**
     * FAQ 삭제(단일일)
     */
    @PostMapping("/d01")
    @Operation(summary = "FAQ 삭제", description = "FAQ 삭제")
    public Integer deleteFaq(@RequestBody List<FaqDeleteReqDto> list) {
        return service.deleteFaq(list);
    }
}
