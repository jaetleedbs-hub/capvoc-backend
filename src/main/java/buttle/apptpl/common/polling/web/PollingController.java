package buttle.apptpl.common.polling.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import buttle.apptpl.common.polling.dto.PollingDto;
import buttle.apptpl.common.polling.service.PollingService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/polling")
@Tag(name = "PollingController", description = "admin 폴링 조회")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class PollingController {
    
    private final PollingService pollingService;

    @GetMapping("/all")
    @Operation(summary = "admin 폴링 조회", description = "admin 폴링 조회")    
    public ResponseEntity<PollingDto> selectPolling( 
        @RequestParam(required = true, name = "memberId") String memberId,
        @RequestParam(required = true, name = "cmpyId") String cmpyId) {
        return ResponseEntity.ok(pollingService.selectPolling(cmpyId, memberId));
    }

}
