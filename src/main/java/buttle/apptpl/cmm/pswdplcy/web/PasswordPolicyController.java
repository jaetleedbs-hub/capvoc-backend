package buttle.apptpl.cmm.pswdplcy.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import buttle.apptpl.cmm.pswdplcy.dto.SYS5020001Dto;
import buttle.apptpl.cmm.pswdplcy.dto.SYS5020002Dto;
import buttle.apptpl.cmm.pswdplcy.service.PasswordPolicyService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/pswd-plcy")
@Tag(name = "PasswordPolicyController", description = "비밀번호정책관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class PasswordPolicyController {

    private final PasswordPolicyService service;

    /**
     * 비밀번호 정책 조회
     * @param dto 조회 조건
     * @return 비밀번호 정책
     */
    @PostMapping("/r01")
    @Operation(summary = "비밀번호 정책 조회", description = "비밀번호 정책 조회")
    public SYS5020002Dto selectPasswordPolicy(@RequestBody SYS5020001Dto dto) {
        return service.selectPasswordPolicy(dto);
    }

    /**
     * 비밀번호 정책 저장
     * @param dto 저장할 비밀번호 정책 정보
     * @return 저장된 비밀번호 정책 정보
     */
    @PostMapping("/u01")
    @Operation(summary = "비밀번호 정책 저장", description = "비밀번호 정책 저장")
    public SYS5020002Dto updatePasswordPolicy(@RequestBody SYS5020002Dto dto) {
        return service.updatePasswordPolicy(dto);
    }

} 