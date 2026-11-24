package buttle.apptpl.log.userlogin.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.log.userlogin.service.UserLoginHistoryService;
import buttle.apptpl.log.userlogin.dto.SYS6010001Dto;
import buttle.apptpl.log.userlogin.dto.SYS6010002Dto;
import buttle.apptpl.common.exception.vo.ErrorResponse;

/**
 * SYS6010 사용자로그인이력 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/log/user-login")
@Tag(name = "UserLoginHistoryController", description = "사용자로그인이력")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class UserLoginHistoryController {

    private final UserLoginHistoryService service;

    /**
     * 일별 사용자 로그인 이력 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "일별 사용자 로그인 이력 조회", description = "일별 사용자 로그인 이력 조회")
    public Page<SYS6010002Dto> selectDailyUserLoginHistList(@RequestBody SYS6010001Dto dto, Pageable pageable) {
        return service.selectDailyUserLoginHistList(dto, pageable);
    }

    /**
     * 일별 사용자 로그인 이력 엑셀 출력
     */
    // @PostMapping("/e01")
    // @Operation(summary = "일별 사용자 로그인 이력 엑셀 출력", description = "일별 사용자 로그인 이력 엑셀 출력")
    // public Integer exportExcelDailyUserLoginHistList(@RequestBody SYS6010001Dto dto) {
    //     return service.exportExcelDailyUserLoginHistList(dto);
    // }
} 