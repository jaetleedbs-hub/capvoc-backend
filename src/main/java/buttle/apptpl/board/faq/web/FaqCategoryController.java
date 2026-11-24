package buttle.apptpl.board.faq.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.api.shared.dto.CategoryDeleteReqDto;
import buttle.apptpl.api.shared.dto.CategoryListReqDto;
import buttle.apptpl.api.shared.dto.FaqCategoryResDto;
import buttle.apptpl.api.shared.dto.CategorySaveReqDto;
import buttle.apptpl.board.faq.service.FaqCategoryService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * BO302_P1 FAQ 분류 관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board/faq-category")
@Tag(name = "FaqCategoryController", description = "FAQ 분류 관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class FaqCategoryController {

    private final FaqCategoryService service;

    /**
     * FAQ 분류 목록 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "FAQ 분류 목록 조회", description = "FAQ 분류 목록 조회")
    public List<FaqCategoryResDto> selectCategoryList(@RequestBody CategoryListReqDto dto) {
        return service.selectCategoryList(dto);
    }

    /**
     * FAQ 분류 저장 (등록/수정)
     */
    @PostMapping("/u01")
    @Operation(summary = "FAQ 분류 저장", description = "FAQ 분류 저장 (등록/수정)")
    public Integer saveCategory(@RequestBody CategorySaveReqDto dto) {
        return service.saveCategory(dto);
    }

    /**
     * FAQ 분류 삭제
     */
    @PostMapping("/d01")
    @Operation(summary = "FAQ 분류 삭제", description = "FAQ 분류 삭제")
    public Integer deleteCategory(@RequestBody CategoryDeleteReqDto dto) {
        return service.deleteCategory(dto);
    }
}
