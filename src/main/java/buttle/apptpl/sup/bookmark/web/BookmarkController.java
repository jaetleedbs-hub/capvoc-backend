package buttle.apptpl.sup.bookmark.web;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import buttle.apptpl.sup.bookmark.dto.COM2070001Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070002Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070003Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070004Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070005Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070006Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070007Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070008Dto;
import buttle.apptpl.sup.bookmark.service.BookmarkService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sup/bookmark")
@Tag(name = "BookmarkController", description = "슬라이드프레임_즐겨찾기 등록")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class BookmarkController {

    private final BookmarkService service;

    @PostMapping("/u01")
    @Operation(summary = "즐겨찾기 저장", description = "즐겨찾기 저장")
    public COM2070002Dto insertBookmark(@RequestBody COM2070001Dto dto) {
        return service.insertBookmark(dto);
    }

    @GetMapping("/r01")
    @Operation(summary = "즐겨찾기 카테고리 목록 조회", description = "즐겨찾기 카테고리 목록 조회")
    public List<COM2070003Dto> selectBookmarkCategoryList() {
        return service.selectBookmarkCategoryList();
    }

    @PostMapping("/u02")
    @Operation(summary = "즐겨찾기 카테고리 저장", description = "즐겨찾기 카테고리 저장")
    public COM2070005Dto insertBookmarkCategory(@RequestBody COM2070004Dto dto) {
        return service.insertBookmarkCategory(dto);
    }

    @PostMapping("/d01")
    @Operation(summary = "즐겨찾기 카테고리 삭제", description = "즐겨찾기 카테고리 삭제")
    public COM2070007Dto deleteBookmarkCategory(@RequestBody COM2070006Dto dto) {
        return service.deleteBookmarkCategory(dto);
    }

    @GetMapping("/r02")
    @Operation(summary = "즐겨찾기 목록 조회", description = "즐겨찾기 목록 조회")
    public List<COM2070008Dto> selectBookmarkList() {
        return service.selectBookmarkList();
    }
} 