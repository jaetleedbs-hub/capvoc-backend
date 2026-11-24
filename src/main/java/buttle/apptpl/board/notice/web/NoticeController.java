package buttle.apptpl.board.notice.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import buttle.apptpl.api.shared.dto.*;
import buttle.apptpl.board.notice.service.NoticeService;
import buttle.apptpl.common.framework.common.util.SecurityUtils;

import java.util.List;

/**
 * BO301_S 시스템 공지사항 관리 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board/notice")
@Tag(name = "NoticeController", description = "시스템 공지사항 관리")
public class NoticeController {

    private final NoticeService service;

    @PostMapping("/notice/upload")
    @Operation(summary = "공지사항파일업로드", description = "공지사항 첨부파일 업로드")
    public NoticeFileUploadResDto uploadNoticeFile(
            @Parameter(name = "file", description = "업로드할 파일", required = true)
            @RequestParam(name = "file") MultipartFile file) {
        log.debug("파일 업로드 요청 - fileName: {}", file.getOriginalFilename());
        return service.uploadNoticeFile(file);
    }

    @GetMapping("/notice")
    @Operation(summary = "공지사항목록조회", description = "검색 조건에 따른 공지사항 목록 조회")
    public List<NoticeListResDto> getNoticeList(
            @Parameter(name = "bbsId", description = "게시판ID", example = "NOTICE")
            @RequestParam(name = "bbsId", required = false) String bbsId,
            @Parameter(name = "bbsStYmd", description = "게시 시작일 (YYYYMMDD)")
            @RequestParam(name = "bbsStYmd", required = false) String bbsStYmd,
            @Parameter(name = "bbsEdYmd", description = "게시 종료일 (YYYYMMDD)")
            @RequestParam(name = "bbsEdYmd", required = false) String bbsEdYmd,
            @Parameter(name = "ttl", description = "제목 검색어")
            @RequestParam(name = "ttl", required = false) String ttl,
            @Parameter(name = "viewUseYn", description = "노출여부 (Y:노출, N:미노출)")
            @RequestParam(name = "viewUseYn", required = false) String viewUseYn) {
        
        log.debug("공지사항 목록조회 요청 - bbsId: {}, bbsStYmd: {}, bbsEdYmd: {}, ttl: {}, viewUseYn: {}", 
                  bbsId, bbsStYmd, bbsEdYmd, ttl, viewUseYn);
        
        NoticeSearchReqDto dto = NoticeSearchReqDto.builder()
                .bbsId(bbsId)
                .bbsStYmd(bbsStYmd)
                .bbsEdYmd(bbsEdYmd)
                .ttl(ttl)
                .viewUseYn(viewUseYn)
                .build();
        
        List<NoticeListResDto> result = service.getNoticeList(dto);
        log.debug("공지사항 목록조회 응답 - 건수: {}", result != null ? result.size() : 0);
        
        return result;
    }

    @GetMapping("/notice/{id}")
    @Operation(summary = "공지사항상세조회", description = "게시물ID로 공지사항 상세 정보 조회")
    public NoticeDetailResDto getNoticeDetail(
            @Parameter(name = "id", description = "게시물ID", required = true)
            @PathVariable(name = "id") String pstId) {
        log.debug("공지사항 상세조회 요청 - pstId: {}", pstId);
        return service.getNoticeDetail(pstId);
    }

    @PostMapping("/notice")
    @Operation(summary = "공지사항저장", description = "공지사항 신규 등록 또는 수정")
    public NoticeCommonResDto saveNotice(
            @Parameter(name = "notice", description = "공지사항 데이터", required = true)
            @RequestBody NoticeSaveReqDto dto) {
        
        log.debug("공지사항 저장 요청 - pstId: {}, atchFileGroupId: {}", 
                  dto.getPstId(), 
                  dto.getAtchFileGroupId());
        
        // 현재 로그인한 사용자 ID 설정
        String currentUserId = SecurityUtils.getCurrentUserId();
        dto.setGdsUserId(currentUserId);
        log.debug("현재 사용자 ID: {}", currentUserId);
        
        return service.saveNotice(dto);
    }

    @PostMapping("/notice/{id}")
    @Operation(summary = "공지사항삭제", description = "게시물ID로 공지사항 삭제")
    public NoticeCommonResDto deleteNotice(
            @Parameter(name = "id", description = "게시물ID", required = true)
            @PathVariable(name = "id") String pstId) {
        log.debug("공지사항 삭제 요청 - pstId: {}", pstId);
        return service.deleteNotice(pstId);
    }
}
