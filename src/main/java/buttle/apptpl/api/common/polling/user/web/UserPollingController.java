package buttle.apptpl.api.common.polling.user.web;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.util.CoreUtils.webutils;
import buttle.apptpl.api.common.polling.user.dto.NoticeDto;
import buttle.apptpl.api.common.polling.user.dto.UserPollingDto;
import buttle.apptpl.api.common.polling.user.dto.UserSummaryDto;
import buttle.apptpl.api.common.polling.user.service.UserPollingService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

/**
 * 사용자 세션 확인 및 갱신 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/common/polling")
@Tag(name = "HealthCheckController", description = "사용자 세션 확인 및 갱신")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

@Slf4j
public class UserPollingController {
   
    private final UserPollingService service;

    @GetMapping("/user-summary")
    @Operation(summary = "사용자 요약 조회", description = "사용자 요약 조회")
    public UserSummaryDto userSummary(HttpServletRequest request) {
        return service.getUserSummary();
    }

    @GetMapping("/user-session")
    @Operation(summary = "사용자 세션 확인 및 갱신", description = "사용자 세션 확인 및 갱신")
    public void userSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.userSession( webutils.getRemoteIp(request));
    }

    @GetMapping("/notification")
    @Operation(summary = "공지목록 조회", description = "공지목록 조회")
    public UserPollingDto notice(HttpServletRequest request) throws IOException {
        return service.getNotices();
    } 

}