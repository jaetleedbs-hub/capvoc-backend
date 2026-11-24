package buttle.apptpl.cmm.authrt.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import buttle.apptpl.cmm.authrt.dto.SYS3020001Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3020002Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3020003Dto;
import buttle.apptpl.cmm.authrt.service.AuthorityGradeMenuService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * 사용자등급별권한관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/authrt-grade")
@Tag(name = "AuthorityGradeMenuController", description = "사용자등급별권한관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class AuthorityGradeMenuController {

    private final AuthorityGradeMenuService service;

    /**
     * 사용자 권한 등급 목록 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "사용자 권한 등급 목록 조회", description = "사용자 권한 등급 목록 조회")
    public List<SYS3020002Dto> selectUserAuthListList(@RequestBody SYS3020001Dto dto) {
        return service.selectUserAuthListList(dto);
    }

    /**
     * 사용자 권한 등급 별 메뉴목록 조회
     */
    @PostMapping("/r02")
    @Operation(summary = "사용자 권한 등급 별 메뉴목록 조회", description = "사용자 권한 등급 별 메뉴목록 조회")
    public List<SYS3020003Dto> selectUserAuthTypeMenuList(@RequestBody SYS3020001Dto dto) {
        return service.selectUserAuthTypeMenuList(dto);
    }

    /**
     * 사용자 권한 등급 별 메뉴목록 저장
     */
    @PostMapping("/u01")
    @Operation(summary = "사용자 권한 등급 별 메뉴목록 저장", description = "사용자 권한 등급 별 메뉴목록 저장")
    public Integer updateUserAuthTypeMenuList(@RequestBody List<SYS3020003Dto> dtoList) {
        return service.updateUserAuthTypeMenuList(dtoList);
    }
} 