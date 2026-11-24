package buttle.apptpl.log.error.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import buttle.apptpl.common.exception.vo.ErrorResponse;
import buttle.apptpl.log.error.dto.SYS6020001Dto;
import buttle.apptpl.log.error.dto.SYS6020002Dto;
import buttle.apptpl.log.error.dto.SYS6020003Dto;
import buttle.apptpl.log.error.service.ErrorLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * SYS6020 오류로그 Controller
 *
 * <p>admin 에 있던 로그 화면 다섯(접속·배치·로그인·사용자변경·회원사) 옆의 <b>여섯 번째</b>다.
 * 그 다섯과 같은 관용구를 쓴다 — {@code POST /r01} · {@code Page<...Dto>}.
 *
 * <p>⚠ {@code cmpt_prgrm} + {@code cmpt_prgrm_authrt} 등록이 <b>함께 가야 한다.</b>
 * 안 하면 403 이다 — §12-9 에서 그 자리에 막혔고, {@code refresh-cache} 도 같은 이유로 못 쓴다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/log/error")
@Tag(name = "ErrorLogController", description = "오류로그")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class ErrorLogController {

    private final ErrorLogService service;

    /** 오류로그 목록 조회 */
    @PostMapping("/r01")
    @Operation(summary = "오류로그 목록 조회", description = "오류로그 목록 조회")
    public Page<SYS6020002Dto> selectErrorLogList(@RequestBody SYS6020001Dto dto, Pageable pageable) {
        return service.selectErrorLogList(dto, pageable);
    }

    /** 오류로그 상세 조회 — 사유 전문은 여기서만 내려간다. */
    @PostMapping("/r02/{logSid}")
    @Operation(summary = "오류로그 상세 조회", description = "오류로그 상세 조회")
    public SYS6020003Dto selectErrorLog(@PathVariable("logSid") Integer logSid) {
        return service.selectErrorLog(logSid);
    }
}
