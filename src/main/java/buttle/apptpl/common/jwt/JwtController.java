package buttle.apptpl.common.jwt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.exception.InvalidationException;
import micoai.corebase.util.CoreUtils;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "JWT Authentication", description = "JWT 토큰 관리 API")
public class JwtController {

    private final JwtProvider jwtProvider;

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token을 사용하여 Access Token을 갱신합니다.")
    @ApiResponse(responseCode = "200", description = "토큰 갱신 성공")
    @ApiResponse(responseCode = "400", description = "토큰 갱신 실패", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 헤더에서 Refresh Token 추출
            String refreshToken = jwtProvider.resolveRefreshToken(request);
            
            if (CoreUtils.string.isBlank(refreshToken)) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"Refresh token not found\", \"message\": \"Refresh-Token 헤더가 필요합니다.\"}");
            }

            // Refresh Token 검증 및 새로운 토큰 생성
            TokenDto newTokens = jwtProvider.refreshTokens(refreshToken);
            
            jwtProvider.addTokenToResponse(response, newTokens);

            return ResponseEntity.ok()
                .body("{\"valid\": true, \"status\": \"OK\", \"message\": \"토큰 갱신 성공\"}");
            
        } catch (InvalidationException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body("{\"error\": \"Token refresh failed\", \"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            return ResponseEntity.badRequest()
                .body("{\"error\": \"Token refresh failed\", \"message\": \"토큰 갱신 중 오류가 발생했습니다.\"}");
        }
    }

    @PostMapping("/refresh-access-only")
    @Operation(summary = "Access Token만 갱신", description = "Refresh Token을 사용하여 Access Token만 갱신합니다.")
    @ApiResponse(responseCode = "200", description = "Access Token 갱신 성공")
    @ApiResponse(responseCode = "400", description = "토큰 갱신 실패", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 헤더에서 Refresh Token 추출
            String refreshToken = jwtProvider.resolveRefreshToken(request);
            
            if (CoreUtils.string.isBlank(refreshToken)) {
                return ResponseEntity.badRequest()
                    .body("{\"error\": \"Refresh token not found\", \"message\": \"Refresh-Token 헤더가 필요합니다.\"}");
            }

            // 새로운 Access Token 생성
            TokenDto tokenDto = jwtProvider.generateAccessTokenDto(jwtProvider.refreshAccessToken(refreshToken));
            
            jwtProvider.addTokenToResponse(response, tokenDto);

            return ResponseEntity.ok("{\"valid\": true, \"status\": \"OK\", \"message\": \"토큰 갱신 성공\"}    ");
            
        } catch (InvalidationException e) {
            log.error("Access token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body("{\"error\": \"Access token refresh failed\", \"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Unexpected error during access token refresh", e);
            return ResponseEntity.badRequest()
                .body("{\"error\": \"Access token refresh failed\", \"message\": \"Access Token 갱신 중 오류가 발생했습니다.\"}");
        }
    }

    @PostMapping("/validate-refresh")
    @Operation(summary = "Refresh Token 검증", description = "Refresh Token의 유효성을 검증합니다.")
    @ApiResponse(responseCode = "200", description = "토큰 검증 결과")
    public ResponseEntity<?> validateRefreshToken(HttpServletRequest request) {
        try {
            String refreshToken = jwtProvider.resolveRefreshToken(request);
            
            if (CoreUtils.string.isBlank(refreshToken)) {
                return ResponseEntity.badRequest()
                    .body("{\"valid\": false, \"message\": \"Refresh-Token 헤더가 필요합니다.\"}");
            }

            JwtProvider.JwtValidationType validationType = jwtProvider.validateRefreshToken(refreshToken);
            
            String message = switch (validationType) {
                case NORMAL -> "유효한 Refresh Token입니다.";
                case EXPIRED -> "만료된 Refresh Token입니다.";
                case INVALID -> "유효하지 않은 Refresh Token입니다.";
            };
            
            return ResponseEntity.ok()
                .body("{\"valid\": " + (validationType == JwtProvider.JwtValidationType.NORMAL) + 
                      ", \"status\": \"" + validationType + "\", \"message\": \"" + message + "\"}");
                      
        } catch (InvalidationException e) {
            // JWT 검증 관련 비즈니스 로직 예외
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body("{\"valid\": false, \"error\": \"JWT_VALIDATION_FAILED\", \"message\": \"" + e.getMessage() + "\"}");
                
        } catch (SecurityException e) {
            // 보안 정책 위반 또는 JWT 보안 관련 예외
            log.error("JWT 보안 검증 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body("{\"valid\": false, \"error\": \"SECURITY_VIOLATION\", \"message\": \"보안 정책 위반으로 토큰 검증에 실패했습니다.\"}");
                
        } catch (IllegalArgumentException e) {
            // 잘못된 토큰 형식 또는 파라미터 오류
            log.warn("잘못된 토큰 형식: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body("{\"valid\": false, \"error\": \"INVALID_TOKEN_FORMAT\", \"message\": \"토큰 형식이 올바르지 않습니다.\"}");
                
        } catch (RuntimeException e) {
            // JWT 라이브러리 관련 런타임 예외
            log.error("JWT 처리 중 런타임 오류: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body("{\"valid\": false, \"error\": \"JWT_PROCESSING_ERROR\", \"message\": \"토큰 처리 중 오류가 발생했습니다.\"}");
                
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("토큰 검증 중 예상치 못한 오류 발생", e);
            return ResponseEntity.badRequest()
                .body("{\"valid\": false, \"error\": \"UNEXPECTED_ERROR\", \"message\": \"토큰 검증 중 시스템 오류가 발생했습니다.\"}");
        }
    }
} 