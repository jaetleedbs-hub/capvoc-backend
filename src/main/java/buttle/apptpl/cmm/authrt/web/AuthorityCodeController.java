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
import buttle.apptpl.cmm.authrt.dto.SYS3010001Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3010002Dto;
import buttle.apptpl.cmm.authrt.service.AuthorityCodeService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * 사용자권한코드관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/authrt-cd")
@Tag(name = "AuthorityCodeController", description = "사용자권한코드관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class AuthorityCodeController {

    private final AuthorityCodeService service;

    /**
     * 사용자 권한 코드 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "사용자 권한 코드 조회", description = "사용자 권한 코드 조회")
    public List<SYS3010002Dto> selectUserAuthCdList(@RequestBody SYS3010001Dto dto) {
        return service.selectUserAuthCdList(dto);
    }

    /**
     * 사용자 권한 코드 상세 조회
     */
    @PostMapping("/r02")
    @Operation(summary = "사용자 권한 코드 상세 조회", description = "사용자 권한 코드 상세 조회")
    public SYS3010002Dto selectUserAuthCdDtl(@RequestBody SYS3010001Dto dto) {
        return service.selectUserAuthCdDtl(dto);
    }

    /**
     * 사용자 권한 코드 저장
     */
    @PostMapping("/u01")
    @Operation(summary = "사용자 권한 코드 저장", description = "사용자 권한 코드 저장")
    public SYS3010001Dto updateUserAuthCd(@RequestBody SYS3010002Dto dto) {
        return service.updateUserAuthCd(dto);
    }

    /**
     * 사용자 권한 코드 삭제
     */
    @PostMapping("/d01")
    @Operation(summary = "사용자 권한 코드 삭제", description = "사용자 권한 코드 삭제")
    public SYS3010001Dto deleteUserAuthCd(@RequestBody SYS3010002Dto dto) {
        return service.deleteUserAuthCd(dto);
    }
} 