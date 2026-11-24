package buttle.apptpl.api.common.components.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.api.common.components.service.CommonUserAuthService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/common/user-auth")
@Tag(name = "CommonUserAuthController", description = "공통 사용자 권한 ")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class CommonUserAuthController {

    private final CommonUserAuthService commonUserAuthService;

    @GetMapping("/duplicate-login")
    @Operation(summary = "중복 로그인 상태 업데이트", description = "중복 로그인 상태 업데이트")
    @Parameter(name = "statusMessage", description = "상태 메시지")
    public String updateDuplicationLoginStatus(@RequestParam(name="statusMessage", required = true) String statusMessage) {
        return commonUserAuthService.updateDuplicationLoginStatus(statusMessage);
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃")
    @Parameter(name = "statusMessage", description = "상태 메시지")
    public String logout() {
        commonUserAuthService.userLogout(SecurityUtils.getCurrentUserId());
        return "ok";
    }


}
