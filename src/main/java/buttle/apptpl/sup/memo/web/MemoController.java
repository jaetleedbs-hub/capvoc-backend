package buttle.apptpl.sup.memo.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import buttle.apptpl.sup.memo.dto.COM2080001Dto;
import buttle.apptpl.sup.memo.dto.COM2080002Dto;
import buttle.apptpl.sup.memo.dto.COM2080003Dto;
import buttle.apptpl.sup.memo.dto.COM2080004Dto;
import buttle.apptpl.sup.memo.service.MemoService;
import buttle.apptpl.common.exception.vo.ErrorResponse;
import io.swagger.v3.oas.annotations.Parameter;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sup/memo")
@Tag(name = "MemoController", description = "슬라이드프레임_메모 등록")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class MemoController {

    private final MemoService service;

    /**
     * 메모 저장
     */
    @PostMapping("/u01")
    @Operation(summary = "저장", description = "저장")
    public COM2080001Dto updateMemo(@RequestBody COM2080001Dto dto) {
        return service.updateMemo(dto);
    }

    // 슬라이드프레임_메모 카테고리 등록
    @PostMapping("/u02")
    @Operation(summary = "슬라이드프레임_메모 카테고리 등록", description = "슬라이드프레임_메모 카테고리 등록")
    public COM2080003Dto insertMemoCategory(@RequestBody COM2080003Dto dto) {
        return service.insertMemoCategory(dto);
    }   

    /**
     * 슬라이드프레임_메모 등록 조회
     */
    @GetMapping("/r01")
    @Parameter(name = "memoId", description = "메모 ID")
    @Operation(summary = "슬라이드프레임_메모 등록 조회", description = "슬라이드프레임_메모 등록 조회")
    public COM2080001Dto selectMemo(@RequestParam(required = true, name="memoId") String memoId,
                                    @RequestParam(required = true, name="memoCtgySid") String memoCtgySid) {
        return service.selectMemo(memoId, memoCtgySid);
    }

    /**
     * 슬라이드프레임_메모 등록 목록조회
     */
    @GetMapping("/r02")
    @Operation(summary = "슬라이드프레임_메모 등록 목록조회", description = "슬라이드프레임_메모 등록 목록조회")
    public List<COM2080002Dto> selectMemoList() {
        return service.selectMemoList();
    }

    /**
     * 슬라이드프레임_메모 카테고리 목록조회
     */
    @GetMapping("/r03")
    @Operation(summary = "슬라이드프레임_메모 카테고리 목록조회", description = "슬라이드프레임_메모 카테고리 목록조회")
    public List<COM2080004Dto> selectMemoCategory() {
        return service.selectMemoCategory();
    }

    /**
     * 슬라이드프레임_메모 등록 삭제
     */
    @PostMapping("/d01")
    @Operation(summary = "슬라이드프레임_메모 등록 삭제", description = "슬라이드프레임_메모 등록 삭제")
    public Integer deleteMemo(@RequestBody COM2080001Dto dto) {
        return service.deleteMemo(dto);
    }

    /**
     * 슬라이드프레임_메모 카테고리 삭제
     */
    @PostMapping("/d02")
    @Operation(summary = "슬라이드프레임_메모 카테고리 삭제", description = "슬라이드프레임_메모 카테고리 삭제")
    public COM2080001Dto deleteMemoCategory(@RequestBody COM2080001Dto dto) {
        return service.deleteMemoCategory(dto);
    }
} 

