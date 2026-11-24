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
import buttle.apptpl.cmm.authrt.dto.SYS3030001Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3030002Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3030003Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3030004Dto;
import buttle.apptpl.cmm.authrt.service.UserAuthorityService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * 사용자별권한관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/user-authrt")
@Tag(name = "UserAuthorityController", description = "사용자별권한관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class UserAuthorityController {

    private final UserAuthorityService service;

    /**
     * 사용자 별 권한관리 대상 사용자 목록 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "사용자 별 권한관리 대상 사용자 목록 조회", description = "사용자 별 권한관리 대상 사용자 목록 조회")
    public List<SYS3030002Dto> selectUserTypeAuthMgmtUserList(@RequestBody SYS3030001Dto dto) {
        return service.selectUserTypeAuthMgmtUserList(dto);
    }

    /**
     * 사용자 별 메뉴 목록 조회
     */
    @PostMapping("/r02")
    @Operation(summary = "사용자 별 메뉴 목록 조회", description = "사용자 별 프로그램 목록 조회")
    public List<SYS3030003Dto> selectUserTypePgmList(@RequestBody SYS3030001Dto dto) {
        return service.selectUserMenuAuthList(dto);
    }

    /**
     * 사용자 별 프로그램 권한 저장
     */
    @PostMapping("/u01")
    @Operation(summary = "사용자 별 메뉴뉴 권한 저장", description = "사용자 별 프로그램 권한 저장")
    public Integer updateUserTypePgmAuth(@RequestBody List<SYS3030003Dto>  dtoList) {
        return service.updateUserTypePgmAuth(dtoList);
    }

    /**
     * 사용자 별 메뉴 권한 삭제
     */
    @PostMapping("/d01")
    @Operation(summary = "사용자 별 메뉴 권한 삭제", description = "사용자 별 프로그램 권한 삭제")
    public Integer deleteUserTypePgmAuth(@RequestBody List<SYS3030003Dto> dtoList) {
        return service.deleteUserTypePgmAuth(dtoList);
    }
    
    /**
     * 전체 메뉴 목록 조회
     */
    @PostMapping("/r03")
    @Operation(summary = "전체 메뉴 목록 조회", description = "전체 메뉴 목록 조회")
    public List<SYS3030003Dto> selectAllMenuList(@RequestBody SYS3030001Dto dto) {
        return service.selectAllMenuList(dto);
    }

    /**
     * 전체 프로그램 목록 조회
     */
    @PostMapping("/r04")
    @Operation(summary = "전체 프로그램 목록 조회", description = "전체 프로그램 목록 조회")
    public List<SYS3030004Dto> selectAllProgramList(@RequestBody SYS3030001Dto dto) {
        return service.selectAllProgramList(dto);
    }

    /**
     * 사용자별 프로그램 권한 목록 조회
     */
    @PostMapping("/r06")
    @Operation(summary = "사용자별 프로그램 권한 목록 조회", description = "사용자별 프로그램 권한 목록 조회")
    public List<SYS3030004Dto> selectUserProgramAuthList(@RequestBody SYS3030001Dto dto) {
        return service.selectUserProgramAuthList(dto);
    }

    /**
     * 사용자별 프로그램 권한 저장
     */
    @PostMapping("/u02")
    @Operation(summary = "사용자별 프로그램 권한 저장", description = "사용자별 프로그램 권한 저장")
    public Integer updateUserTypeProgramAuth(@RequestBody List<SYS3030004Dto> dtoList) {
        return service.updateUserTypeProgramAuth(dtoList);
    }

    /**
     * 사용자별 프로그램 권한 삭제
     */
    @PostMapping("/d02")
    @Operation(summary = "사용자별 프로그램 권한 삭제", description = "사용자별 프로그램 권한 삭제")
    public Integer deleteUserTypeProgramAuth(@RequestBody List<SYS3030004Dto> dtoList) {
        return service.deleteUserTypeProgramAuth(dtoList);
    }



} 