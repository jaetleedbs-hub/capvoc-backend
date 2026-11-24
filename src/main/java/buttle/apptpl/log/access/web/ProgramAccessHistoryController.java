package buttle.apptpl.log.access.web;

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
import buttle.apptpl.log.access.dto.SYS6040001Dto;
import buttle.apptpl.log.access.dto.SYS6040002Dto;
import buttle.apptpl.log.access.service.ProgramAccessHistoryService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * SYS6040 프로그램접속이력 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/log/access")
@Tag(name = "ProgramAccessHistoryController", description = "프로그램접속이력")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class ProgramAccessHistoryController {

    private final ProgramAccessHistoryService service;

    /**
     * 프로그램 접속 이력 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "프로그램 접속 이력 조회", description = "프로그램 접속 이력 조회")
    public Page<SYS6040002Dto> selectPgmConnHistList(@RequestBody SYS6040001Dto dto, Pageable pageable) {
        return service.selectPgmConnHistList(dto, pageable);
    }

} 