package buttle.apptpl.common.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.util.CoreUtils;
import buttle.apptpl.api.common.components.service.CommonUserAuthService;
import buttle.apptpl.common.security.SvcEnvConfig;

@Slf4j
@Data
@NoArgsConstructor
public class JwtProvider {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    public static final String ACCESS_TOKEN_EXPIRE_TIME = "ACCESS_TOKEN_EXPIRE_TIME";
    public static final String REFRESH_TOKEN_EXPIRE_TIME = "REFRESH_TOKEN_EXPIRE_TIME";
    public static final String ANONYMOUS_MODE = "ANONYMOUS_MODE";
    public static final String HEADER_PREFIX = "Bearer ";
    // [크로스앱 격리] 쿠키는 host 로만 격리되고 포트로는 격리되지 않는다. connect/desk/admin 이 같은 host
    // 에서 동일 이름 쿠키를 쓰면 마지막 로그인이 이전 앱 쿠키를 덮어써 이전 앱이 로그아웃된다.
    // 앱별 접두어로 네임스페이스를 분리한다.
    public static final String ACCESS_TOKEN_COOKIE = "admin_accessToken";
    public static final String REFRESH_TOKEN_COOKIE = "admin_refreshToken";

    private static final String AUTHORITIES_KEY = "auth";
    private static final String PRINCIPAL_KEY = "principal";
    private static final String BEARER_TYPE = "Bearer";

    private Long accessTokenValidityInSeconds;
    private Long refreshTokenValidityInSeconds;
    private Boolean cookieSecure;

    private SecretKey key;

    @Autowired
    private SvcEnvConfig svcEnvConfig;

    public enum JwtValidationType {
        INVALID, EXPIRED, NORMAL
    }

    @Autowired
    private CommonUserAuthService commonUserAuthService;

    @Autowired
    private JwtRepository jwtRepository;

    @PostConstruct
    public void init() {
        // this.jwtConfig1 = svcEnvConfig.getJwt();
        byte[] keyBytes = Decoders.BASE64.decode(svcEnvConfig.getJwt().getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInSeconds = svcEnvConfig.getJwt().getAccessTokenValidityInSeconds();
        this.refreshTokenValidityInSeconds = svcEnvConfig.getJwt().getRefreshTokenValidityInSeconds();
        if( svcEnvConfig.getJwt().getCookieSecure() == null){
            this.cookieSecure = false;
        } else {
            this.cookieSecure = svcEnvConfig.getJwt().getCookieSecure();
        }

        log.info("===============================================");
        log.info("ACCESS TOKEN EXPIRE TIME:" + this.accessTokenValidityInSeconds + " seconds");
        log.info("REFRESH TOKEN EXPIRE TIME:" + this.refreshTokenValidityInSeconds + " seconds");
        log.info("===============================================");
    }

    /**
     * Access Token 만료 시간 조회
     * @return
     */
    public Long getAccessTokenValidityInSeconds() {
        return accessTokenValidityInSeconds;
    }


    
    /**
     * Refresh Token 만료 시간 조회
     * @return
     */
    public Long getRefreshTokenValidityInSeconds() {
        return refreshTokenValidityInSeconds;
    }


    public boolean isAnonymousMode() {
        try {
            boolean anonymousMode = svcEnvConfig.getJwt().getAnonymousMode();
            log.debug("Anonymous mode from config: {}", anonymousMode);
            return anonymousMode;
            
        } catch (NullPointerException e) {
            // 설정 객체가 null인 경우
            log.warn("JWT 설정 정보가 null입니다. Anonymous mode를 false로 설정합니다: {}", e.getMessage());
            return false;
            
        } catch (IllegalStateException e) {
            // 설정 상태가 올바르지 않은 경우
            log.warn("JWT 설정 상태가 올바르지 않습니다. Anonymous mode를 false로 설정합니다: {}", e.getMessage());
            return false;
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("Anonymous mode 확인 중 예상치 못한 오류 발생, false로 기본 설정: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Request header 또는 쿠키에서 Access token을 구한다.
     * 우선순위: 1. 쿠키 2. Authorization 헤더
     * @param request
     * @return
     */
    public String resolveAccessToken(HttpServletRequest request) {
        // 1. 먼저 쿠키에서 Access Token을 찾습니다.
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    String accessToken = cookie.getValue();
                    if (StringUtils.isNotEmpty(accessToken)) {
                        return accessToken;
                    }
                }
            }
        }
        
        // 2. 쿠키에서 찾지 못했다면 Authorization 헤더에서 찾습니다. (하위 호환성)
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(CoreUtils.string.length(HEADER_PREFIX));
        }
        
        return null;
    }


    /**
     * HTTP Request 쿠키에서 Refresh Token을 구한다.
     * @param request HttpServletRequest 객체
     * @return 쿠키에서 찾은 Refresh Token 문자열 또는 null
     */
    public String resolveRefreshToken(HttpServletRequest request) {
        // 모든 쿠키를 가져옵니다.
        Cookie[] cookies = request.getCookies();

        
        // 쿠키가 존재하고 비어있지 않은 경우에만 처리합니다.
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                // 쿠키 이름이 앱별 refresh 쿠키인 것을 찾습니다.
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    // 쿠키 값은 보통 "Bearer " 접두사를 포함하지 않으므로, 이 부분은 제거
                    // 필요하다면, 여기서 토큰 값의 유효성을 간단히 검사할 수 있습니다.
                    if (StringUtils.isNotEmpty(refreshToken)) { // StringUtils.hasText()는 null, 빈 문자열, 공백만 있는 문자열을 체크
                        return refreshToken;
                    }
                }
            }
        }
        
        return null; // Refresh Token 쿠키를 찾지 못했거나 값이 비어있을 경우
    }

    private TokenDto generateTokenDto(String accessToken, String refreshToken) {
        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(getAccessTokenValidityInSeconds())
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(getRefreshTokenValidityInSeconds())
                .build();
    }


    public TokenDto generateAccessTokenDto(String accessToken) {
        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(getAccessTokenValidityInSeconds())
                .build();
    }




    /**
     * Token 생성
     * @param authentication
     * @return TokenDto
     */
    public TokenDto generateToken(SecUser member) {

        // Access Token 생성
        String accessToken = createAccessToken(member);

        // Refresh Token 생성
        String refreshToken = createRefreshToken(member);

        return generateTokenDto(accessToken, refreshToken);
        
    }




    public Authentication getAuthentication(String accessToken, String refreshToken) {

        // 2. Refresh Token이 없는 경우 (Access Token만 있는 경우)
        if( StringUtils.isEmpty(refreshToken)) {
            log.warn("Refresh Token이 없음 - 재로그인 필요");
            throw new JwtTokenException(473, "인증 정보가 불완전합니다. 다시 로그인하여 주십시오.");
        }

        JwtValidationType validationType = validateToken(accessToken);
        
        // Access Token이 null이거나 빈 문자열인 경우 처리
        if (validationType != JwtValidationType.NORMAL) {
            log.debug("Access Token이 없음 - Refresh Token 검증 후 갱신 필요 ");
            
            // Refresh Token 검증
            JwtValidationType refreshValidationType = validateRefreshToken(refreshToken);
            if (refreshValidationType == JwtValidationType.NORMAL) {
                throw new JwtTokenException(471, "Access Token이 없습니다. 갱신하여 주십시오.");
            } else if (refreshValidationType == JwtValidationType.EXPIRED) {
                throw new JwtTokenException(473, "로그인 세션이 만료되었습니다. 다시 로그인하여 주십시오.");
            } else {
                throw new JwtTokenException(474, "Refresh Token이 올바르지 않습니다. 다시 로그인하여 주십시오.");
            }
        }

        // 캐시에서 토큰 조회
        Authentication authentication = jwtRepository.checkRefreshToken(refreshToken);
        if( authentication != null) {
            return authentication;
        }

        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new JwtTokenException("인증정보에 권한정보가 없어 작업을 중단하였습니다.");
        }

        String strAuthorities = claims.get(AUTHORITIES_KEY).toString();
        if (CoreUtils.string.isBlank(strAuthorities)) {
            throw new JwtTokenException("인증정보에 권한정보가 없어 작업을 중단하였습니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        Object principal = claims.get(PRINCIPAL_KEY);
        if (principal == null) {
            throw new JwtTokenException("인증정보에 사용자 정보가 없어 작업을 중단하였습니다.");
        }

        @SuppressWarnings("unchecked")
        HashMap<String, Object> map = (HashMap<String, Object>)principal;
        SecUser member = CoreUtils.json.mapToObject(map, SecUser.class);
        jwtRepository.saveRefreshToken(refreshToken, member);

        // UserDetails 객체를 만들어서 Authentication 리턴
        return new UsernamePasswordAuthenticationToken(member, "", authorities);
    }








    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public JwtValidationType validateToken(String token) {
        if (StringUtils.isEmpty(token)) {
            log.debug("Token is empty or null");
            return JwtValidationType.INVALID;
        }
        
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            // 토큰 만료 시간 로깅
            Date expiration = claims.getExpiration();
            Date now = new Date();
            log.debug("Token expiration: {}, Current time: {}, Is expired: {}", expiration, now, now.after(expiration));
            
            return JwtValidationType.NORMAL;
        } catch(SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return JwtValidationType.INVALID;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token - Issued at: {}, Expired at: {}, Current: {}", 
                e.getClaims().getIssuedAt(), e.getClaims().getExpiration(), new Date());
            return JwtValidationType.EXPIRED;
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            return JwtValidationType.INVALID;
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
            return JwtValidationType.INVALID;
        }
    }

    /**
     * Access Token이 만료 임박 상태인지 확인 (만료 10분 전)
     * @param token Access Token
     * @return 만료 10분 이내면 true, 그 외 false
     */
    public boolean isTokenExpiringSoon(String token, int minutes) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        
        try {
            Claims tokenClaims = parseClaims(token);
            Date issuedAt = tokenClaims.getIssuedAt();
            if( issuedAt == null) {
                return false;
            }

            long elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - issuedAt.getTime());
            if( elapsedSeconds >= minutes * 60) {
                return true;
            }
            return false;

        } catch (ExpiredJwtException e) {
            // 이미 만료된 토큰
            log.debug("Token already expired");
            return false;
        } catch (Exception e) {
            log.debug("Error checking token expiration: {}", e.getMessage());
            return false;
        }
    }

    public String createAccessToken(SecUser member) {

        Date nowDt = new Date();
        long now = nowDt.getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + getAccessTokenValidityInSeconds() * 1000);
        
        if(log.isDebugEnabled()){
            log.debug("Creating Access Token for user: {}, Issued at: {}, Expires at: {}, Validity: {} seconds", member.getUserId(), nowDt, accessTokenExpiresIn, getAccessTokenValidityInSeconds());
        }
        
        return Jwts.builder()
                .issuedAt(nowDt)
                .subject(member.getUserId())             // payload "sub": "id"
                // [N-15] 권한을 "ROLE_USER" 로 고정 발급하던 것을 실제 권한으로 교체.
                // 인가 판단은 FwAuthorizationManager 가 principal(SecUser)로 하므로 이 클레임은
                // 인가에 쓰이지 않지만, 실제와 다른 값이 박혀 있으면 설계 혼란·오용을 부른다.
                .claim(AUTHORITIES_KEY, resolveAuthorities(member))
                .claim(PRINCIPAL_KEY, member)
                .expiration(accessTokenExpiresIn)       // payload "exp": 1516239022 (예시)
                .signWith(key)
                .compact();
    }

    /**
     * 액세스 토큰 {@code auth} 클레임에 담을 권한 문자열.
     * 실제 권한(authrtCdList → authrtCd)을 우선하고, 둘 다 없을 때만 최소 권한으로 떨어진다.
     * 파싱 측({@link #getAuthentication})이 빈 값을 예외 처리하므로 빈 문자열은 반환하지 않는다.
     */
    private String resolveAuthorities(SecUser member) {
        if (member.getAuthrtCdList() != null && !member.getAuthrtCdList().isEmpty()) {
            return String.join(",", member.getAuthrtCdList());
        }
        if (CoreUtils.string.isNotBlank(member.getAuthrtCd())) {
            return member.getAuthrtCd();
        }
        return "ROLE_USER"; // 권한 미상 시 최소 권한(파싱 검증 통과용)
    }

    public String createRefreshToken(SecUser member) {

        Date nowDt = new Date();
        long now = nowDt.getTime();

        // Refresh Token 생성
        Date refreshTokenExpiresIn = new Date(now + getRefreshTokenValidityInSeconds() * 1000);
        
        if( log.isDebugEnabled()){
            log.debug("Creating Refresh Token for user: {}, Issued at: {}, Expires at: {}, Validity: {} seconds", member.getUserId(), nowDt, refreshTokenExpiresIn, getRefreshTokenValidityInSeconds());
        }
        
        return Jwts.builder()
                .issuedAt(nowDt)
                .subject(member.getUserId())
                .expiration(refreshTokenExpiresIn)
                .signWith(key)
                .compact();
    }

    /**
     * Refresh Token 검증
     * @param refreshToken
     * @return
     */
    public JwtValidationType validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken);
    }



    /**
     * Refresh Token으로부터 사용자 정보 추출
     * @param refreshToken
     * @return
     */
    public SecUser getUserFromRefreshToken(String refreshToken) {
        try {
            // 1. 캐시에서 우선 조회
            Authentication authentication = jwtRepository.checkRefreshToken(refreshToken);
            if (authentication != null && authentication.getPrincipal() instanceof SecUser) {
                return (SecUser) authentication.getPrincipal();
            }
            
            // 2. 캐시 미스 시 토큰 파싱
            Claims claims = parseClaims(refreshToken);
            String userId = claims.get("sub", String.class);
            
            SecUser member = commonUserAuthService.getSecUser(userId);
            jwtRepository.saveRefreshToken(refreshToken, member);
            return member;
            
        } catch (ExpiredJwtException e) {
            // JWT 토큰 만료
            log.warn("Refresh Token이 만료되었습니다: userId={}", e.getClaims().getSubject());
            throw new JwtTokenException("Refresh Token이 만료되었습니다. 다시 로그인해주세요.");
            
        } catch (SecurityException | MalformedJwtException e) {
            // JWT 토큰 서명 오류 또는 형식 오류
            log.warn("유효하지 않은 Refresh Token 형식: {}", e.getMessage());
            throw new JwtTokenException("유효하지 않은 Refresh Token입니다. 다시 로그인해주세요.");
            
        } catch (UnsupportedJwtException e) {
            // 지원하지 않는 JWT 토큰
            log.warn("지원하지 않는 JWT 토큰 형식: {}", e.getMessage());
            throw new JwtTokenException("지원하지 않는 토큰 형식입니다. 다시 로그인해주세요.");
            
        } catch (IllegalArgumentException e) {
            // 잘못된 토큰 인수
            log.warn("잘못된 JWT 토큰 인수: {}", e.getMessage());
            throw new JwtTokenException("잘못된 토큰 형식입니다. 다시 로그인해주세요.");
            
        } catch (NullPointerException e) {
            // 토큰이나 클레임이 null인 경우
            log.warn("Refresh Token 또는 클레임 정보가 null입니다: {}", e.getMessage());
            throw new JwtTokenException("토큰 정보가 올바르지 않습니다. 다시 로그인해주세요.");
            
        } catch (ClassCastException e) {
            // 타입 캐스팅 오류 (Principal이 SecUser가 아닌 경우)
            log.warn("인증 정보 타입 변환 오류: {}", e.getMessage());
            throw new JwtTokenException("인증 정보 형식이 올바르지 않습니다. 다시 로그인해주세요.");
            
        } catch (RuntimeException e) {
            // 사용자 정보 조회 실패 등 기타 런타임 예외
            log.error("사용자 정보 조회 중 런타임 오류: {}", e.getMessage(), e);
            throw new JwtTokenException("사용자 정보를 조회할 수 없습니다. 다시 로그인해주세요.");
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("Refresh Token에서 사용자 정보 추출 중 예상치 못한 오류", e);
            throw new JwtTokenException("토큰 처리 중 시스템 오류가 발생했습니다. 다시 로그인해주세요.");
        }
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token 생성
     * @param refreshToken
     * @return
     */
    public String refreshAccessToken(String refreshToken) {
        // Refresh Token 검증
        JwtValidationType validationType = validateRefreshToken(refreshToken);
        if (validationType != JwtValidationType.NORMAL) {
            throw new JwtTokenException("유효하지 않은 Refresh Token입니다.");
        }

        // Refresh Token에서 사용자 정보 추출
        SecUser user = getUserFromRefreshToken(refreshToken);
        
        // 새로운 Access Token 생성
        return createAccessToken(user);
    }

    /**
     * Access Token과 Refresh Token 모두 갱신
     * @param refreshToken
     * @return
     */
    public TokenDto refreshTokens(String refreshToken) {
        // Refresh Token 검증
        JwtValidationType validationType = validateRefreshToken(refreshToken);
        if (validationType != JwtValidationType.NORMAL) {
            throw new JwtTokenException("유효하지 않은 Refresh Token입니다.");
        }

        // Refresh Token에서 사용자 정보 추출
        SecUser user = getUserFromRefreshToken(refreshToken);
        
        // 새로운 토큰들 생성
        String newAccessToken = createAccessToken(user);
        String newRefreshToken = createRefreshToken(user);

        return generateTokenDto(newAccessToken, newRefreshToken);
    }

    public void invalidateToken(HttpServletResponse response, String refreshToken) {

        // 토큰 무효화
        // jwtRepository.invalidateToken(tokenDto.getRefreshToken());
    }

    public void addTokenToResponse(HttpServletResponse response, TokenDto tokenDto) {
    
        // JWT 토큰 발급 (새로운 Access Token과 Refresh Token 생성)
        final String newAccessToken = tokenDto.getAccessToken(); // 이름 변경
        final String newRefreshToken = tokenDto.getRefreshToken(); // 이름 변경
        
        // 1. Access Token을 HttpOnly, Secure 쿠키로 설정
        if( StringUtils.isNotEmpty(newAccessToken)){
            Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE,newAccessToken);
            accessTokenCookie.setHttpOnly(true); // JavaScript 접근 불가
            accessTokenCookie.setSecure(this.cookieSecure); // HTTPS 에서만 전송 (운영 환경에서만 true)
            accessTokenCookie.setPath("/"); // 모든 경로에서 사용 가능
            accessTokenCookie.setMaxAge(getAccessTokenValidityInSeconds().intValue()); // Access Token 만료 시간
            if( this.cookieSecure){
                accessTokenCookie.setAttribute("SameSite", "None");
            }
            response.addCookie(accessTokenCookie); // 응답에 쿠키 추가
        }
        
        // 2. Refresh Token을 HttpOnly, Secure 쿠키로 설정
        if( StringUtils.isNotEmpty(newRefreshToken)){
            Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE,newRefreshToken);
            refreshTokenCookie.setHttpOnly(true); // JavaScript 접근 불가
            refreshTokenCookie.setSecure(this.cookieSecure); // HTTPS 에서만 전송 (운영 환경에서만 true)
            refreshTokenCookie.setPath("/"); // 모든 경로에서 사용 가능
            refreshTokenCookie.setMaxAge(getRefreshTokenValidityInSeconds().intValue()); // Refresh Token 만료 시간
            if( this.cookieSecure){
                refreshTokenCookie.setAttribute("SameSite", "None");
            }
            response.addCookie(refreshTokenCookie); // 응답에 쿠키 추가
        }
        
        // 기존 헤더 방식도 유지 (하위 호환성)
        response.addHeader(AUTHORIZATION_HEADER, "Bearer " + newAccessToken);
    }


    public void responseLogout(HttpServletResponse response) {
        // 1. accessToken 쿠키를 무효화합니다.
        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE,"");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(this.cookieSecure); // addTokenToResponse와 동일하게 설정해야 합니다.
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // Max-Age를 0으로 설정하여 즉시 만료시킵니다.
        response.addCookie(accessTokenCookie);
        
        // 2. refreshToken 쿠키를 무효화합니다.
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE,"");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(this.cookieSecure); // addTokenToResponse와 동일하게 설정해야 합니다.
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // Max-Age를 0으로 설정하여 즉시 만료시킵니다.
        response.addCookie(refreshTokenCookie);
    
        // 클라이언트에게 로그아웃 성공 메시지나 리다이렉션 등의 응답을 보낼 수 있습니다.
        response.setStatus(HttpServletResponse.SC_OK); // 200 OK
    }    
}
