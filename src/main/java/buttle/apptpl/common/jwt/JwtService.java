package buttle.apptpl.common.jwt;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.util.CoreUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;

    /**
     * Refresh Token 자동 갱신 체크 시간 계산
     * Refresh Token 유효시간의 25%를 갱신 체크 시간으로 사용 (최소 30분, 최대 120분)
     * 
     * 예: Refresh Token 240분(4시간)인 경우
     *     → 60분(1시간) 이하 남으면 자동 갱신
     *     → 마지막 활동 후 4시간 동안 세션 유지 (세션 타임아웃과 동일)
     * 
     * @return 갱신 체크 시간 (분)
     */
    private double calculateAutoRefreshThresholdSeconds() {
        
        double refreshTokenSeconds = jwtProvider.getRefreshTokenValidityInSeconds();
        
        // Refresh Token 유효시간의 50%를 분으로 계산
        double thresholdMinutes = refreshTokenSeconds * 0.5;

        return thresholdMinutes;
    }

    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtProvider.resolveRefreshToken(request);
        String accessToken = jwtProvider.resolveAccessToken(request);
        String requestUri = request.getRequestURI();

        log.debug("JWT Authentication - Access Token present: {}, Refresh Token present: {}, Request URI: {}", !StringUtils.isEmpty(accessToken), !StringUtils.isEmpty(refreshToken), requestUri);

        // 1. 익명 모드 체크 (토큰이 모두 없는 경우)
        if (StringUtils.isEmpty(accessToken) && StringUtils.isEmpty(refreshToken)) {
            log.info("getContextTokenAuthentication : 모든 토큰이 없음, requestUrl: {}", requestUri);

            if (jwtProvider.isAnonymousMode()) {
                log.debug("익명 모드로 인증 처리");
                return new AnonymousAuthenticationToken(
                        CoreUtils.string.getNewId(""),
                        "anonymousUser",
                        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
            } else {
                throw new JwtTokenException(473, "로그인이 필요합니다. 다시 로그인하여 주십시오.");
            }
        }

        // 2. 토큰 자동 갱신 체크 (polling URL이 아닌 경우)
        if (!requestUri.startsWith("/api/common/polling")) {
            // Access Token과 Refresh Token이 모두 있는 경우에만 갱신 체크
            if (!StringUtils.isEmpty(accessToken) && !StringUtils.isEmpty(refreshToken)) {
                
                // Refresh Token 검증
                JwtProvider.JwtValidationType refreshValidationType = jwtProvider.validateRefreshToken(refreshToken);
                log.debug("Refresh Token validation - Type: {}, URI: {}", refreshValidationType, requestUri);
                
                // Refresh Token이 유효한 경우에만 갱신 체크
                if (refreshValidationType == JwtProvider.JwtValidationType.NORMAL) {
                    
                    // Refresh Token 잔여 시간이 임계값 이하인 경우 자동 갱신 (세션 타임아웃 연장)
                    if (jwtProvider.isTokenExpiringSoon(refreshToken, 5)) {
                        
                        try {
                            // 새로운 Access Token과 Refresh Token 모두 생성 (Sliding Session 방식)
                            TokenDto tokenDto = jwtProvider.refreshTokens(refreshToken);

                            // 응답에 새 토큰들 추가 (쿠키로 전송)
                            jwtProvider.addTokenToResponse(response, tokenDto);
                            
                            accessToken = tokenDto.getAccessToken();
                            refreshToken = tokenDto.getRefreshToken();
                            
                        } catch (Exception e) {
                            log.error("✗ 토큰 자동 갱신 중 오류 발생: {}, URI: {}", e.getMessage(), requestUri, e);
                            // 갱신 실패 시에도 기존 토큰으로 인증 진행 (아직 유효하므로)
                        }
                    }
                }
            }
        }

        return jwtProvider.getAuthentication(accessToken, refreshToken);
    }

}
