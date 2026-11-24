package buttle.apptpl.board.notice.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.api.shared.dto.NoticePopupDetailReqDto;
import buttle.apptpl.api.shared.dto.NoticePopupDetailResDto;
import buttle.apptpl.board.notice.service.NoticeDetailService;

/**
 * BO301_P1 공지사항 상세 팝업 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board/notice/detail")
@Tag(name = "NoticeDetailController", description = "공지사항 상세 팝업")
public class NoticeDetailController {

    private final NoticeDetailService service;

    @GetMapping("/notice/{pstId}")
    @Operation(summary = "공지사항상세조회(팝업)")
    public NoticePopupDetailResDto getNoticeDetail(@PathVariable("pstId") String pstId) {
        NoticePopupDetailReqDto dto = NoticePopupDetailReqDto.builder()
                .pstId(pstId)
                .build();
        return service.getNoticeDetail(dto);
    }
}
