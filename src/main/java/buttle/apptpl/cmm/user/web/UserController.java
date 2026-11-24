package buttle.apptpl.cmm.user.web;

import java.util.List;

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
import buttle.apptpl.cmm.user.dto.SYS0030001Dto;
import buttle.apptpl.cmm.user.dto.SYS0030002Dto;
import buttle.apptpl.cmm.user.dto.SYS0030003Dto;
import buttle.apptpl.cmm.user.service.UserService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/user")
@Tag(name = "UserController", description = "사용자관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class UserController {

    private final UserService service;

    @PostMapping("/r01")
    @Operation(summary = "사용자 정보 조회", description = "사용자 정보 조회")
    public List<SYS0030001Dto> selectUserInfoList(@RequestBody SYS0030001Dto dto) {
        return service.selectUserInfoList(dto);
    }       

    @PostMapping("/r02")
    @Operation(summary = "사용자 상세 정보 조회", description = "사용자 상세 정보 조회")
    public SYS0030002Dto selectUserDtlInfo(@RequestBody SYS0030002Dto dto) {
        return service.selectUserDtlInfo(dto);
    }

    @PostMapping("/u01")
    @Operation(summary = "사용자정보 저장", description = "사용자정보 저장")
    public SYS0030002Dto updateUserInfo(@RequestBody SYS0030002Dto dto) {
        return service.updateUserInfo(dto);
    }

    @PostMapping("/u02")
    @Operation(summary = "로그인 초기화", description = "로그인 초기화")
    public SYS0030003Dto resetLogin(@RequestBody SYS0030003Dto dto) {
        return service.resetLogin(dto);
    }

    @PostMapping("/u03")
    @Operation(summary = "비밀번호 초기화", description = "비밀번호 초기화")
    public SYS0030003Dto resetPwd(@RequestBody SYS0030003Dto dto) {
        return service.resetPwd(dto);
    }
} 