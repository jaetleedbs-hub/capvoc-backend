package buttle.apptpl.cmm.prgrm.web;

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
import buttle.apptpl.cmm.prgrm.dto.SYS3050001Dto;
import buttle.apptpl.cmm.prgrm.dto.SYS3050002Dto;
import buttle.apptpl.cmm.prgrm.dto.SYS3050003Dto;
import buttle.apptpl.cmm.prgrm.service.ProgramAuthorityService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * SYS3050 API 권한 삭제 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/prgrm-authrt")
@Tag(name = "ProgramAuthorityController", description = "API 권한 삭제")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class ProgramAuthorityController {

    private final ProgramAuthorityService service;

    /**
     * API 권한 목록 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "API 권한 목록 조회", description = "API 권한 목록 조회")
    public List<SYS3050002Dto> selectApAuthList(@RequestBody SYS3050001Dto dto) {
        return service.selectApAuthList(dto);
    }

    /**
     * 사용자 권한 등급 별 프로그램 목록 조회
     */
    @PostMapping("/r02")
    @Operation(summary = "사용자 권한 등급 별 프로그램 목록 조회", description = "사용자 권한 등급 별 프로그램 목록 조회")
    public List<SYS3050003Dto> selectUserAuthTypePrgrmList(@RequestBody SYS3050001Dto dto) {
        return service.selectUserAuthTypePrgrmList(dto);
    }

    /**
     * API 권한 목록 저장
     */
    @PostMapping("/u01")
    @Operation(summary = "API 권한 목록 저장", description = "API 권한 목록 저장")
    public Integer updateApAuthList(@RequestBody List<SYS3050003Dto> dtoList) {
        return service.updateUserAuthTypePrgrmList(dtoList);
    }

} 