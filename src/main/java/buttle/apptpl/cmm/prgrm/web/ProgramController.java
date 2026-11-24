package buttle.apptpl.cmm.prgrm.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.cmm.prgrm.dto.SYS3040001Dto;
import buttle.apptpl.cmm.prgrm.service.ProgramService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * 프로그램관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/prgrm")
@Tag(name = "ProgramController", description = "프로그램관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class ProgramController {

    private final ProgramService service;

    /**
     * API 등록
     */
    @PostMapping("/u01")
    @Operation(summary = "API 등록", description = "API 등록")
    public Integer updateApAuthList(@RequestBody List<SYS3040001Dto> dtoList) {
        return service.updateApAuthList(dtoList);
    }

    /**
     * API 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "API 조회", description = "API 조회")
    public List<SYS3040001Dto> ApAuthList(@RequestBody SYS3040001Dto dto) {
        return service.ApAuthList(dto);
    }

    /**
     * API 삭제
     */
    @PostMapping("/d01")
    @Operation(summary = "API 삭제", description = "API 삭제")
    public Integer deleteApAuthList(@RequestBody List<SYS3040001Dto> dtoList) {
        return service.deleteApAuthList(dtoList);
    }

} 