package buttle.apptpl.log.userchange.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import buttle.apptpl.log.userchange.dto.SYS6030001Dto;
import buttle.apptpl.log.userchange.dto.SYS6030002Dto;
import buttle.apptpl.log.userchange.service.UserChangeHistoryService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

/**
 * SYS6030 사용자변경이력 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/log/user-change")
@Tag(name = "UserChangeHistoryController", description = "사용자변경이력")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class UserChangeHistoryController {

    private final UserChangeHistoryService service;

    /**
     * 사용자 변경 이력 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "사용자 변경 이력 조회", description = "사용자 변경 이력 조회")
    public Page<SYS6030002Dto> selectUserChgHistList(@RequestBody SYS6030001Dto dto, Pageable pageable) {
        return service.selectUserChgHistList(dto, pageable);
    }

    /**
     * 사용자 변경 이력 엑셀 출력
     */
    // @PostMapping("/e01")
    // @Operation(summary = "사용자 변경 이력 엑셀 출력", description = "사용자 변경 이력 엑셀 출력")
    // public List<SYS6030002Dto> exportExcelUserChgHistList(@RequestBody SYS6030001Dto dto) {
    //     return service.exportExcelUserChgHistList(dto);
    // }
} 