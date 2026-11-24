package buttle.apptpl.auth.password.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.auth.password.dto.COM0020001Dto;
import buttle.apptpl.auth.password.dto.COM0020002Dto;
import buttle.apptpl.auth.password.service.PasswordChangeService;
import buttle.apptpl.common.exception.vo.ErrorResponse;
import buttle.apptpl.auth.password.exception.AccountLockedException;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.ArrayList;

/**
 * COM0020 비밀번호변경 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/password")
@Tag(name = "PasswordChangeController", description = "비밀번호변경")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class PasswordChangeController {

    private final PasswordChangeService service;
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 비밀번호 변경
     * @param dto 비밀번호변경 요청
     * @return 비밀번호변경 응답
     */
    @PostMapping("/u01")
    @Operation(summary = "저장", description = "저장")
    public COM0020002Dto updatePassword(@RequestBody COM0020001Dto dto, HttpServletResponse response) {
        try {
            COM0020002Dto result = service.updatePassword(dto);
            return result;
        } catch (AccountLockedException e) {
            // 비밀번호 오류 횟수 초과로 계정 사용 중지 및 로그아웃된 경우 → HTTP 473
            // (일반 InvalidationException 은 여기서 잡지 않고 전역 예외처리로 위임된다)
            try {
                ErrorResponse error = ErrorResponse.builder()
                    .error("Invalid")
                    .status(431)
                    .message(e.getMessage())
                    .errors(new ArrayList<>())
                    .build();

                String errorJson = objectMapper.writeValueAsString(error);

                response.setStatus(473);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().print(errorJson);
                response.flushBuffer();
                return null;
            } catch (IOException ioException) {
                throw new RuntimeException("응답 처리 중 오류가 발생했습니다.", ioException);
            }
        }
    }

    /**
     * 비밀번호 검증
     * @param dto 비밀번호 검증 요청
     * @return 비밀번호 검증 결과
     */
    @PostMapping("/r01")
    @Operation(summary = "비밀번호 검증", description = "비밀번호 검증")
    public COM0020001Dto validatePassword(@RequestBody COM0020001Dto dto) {
        return service.validatePassword(dto);   
    }
} 