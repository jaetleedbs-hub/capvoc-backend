package buttle.apptpl.common.config.caffeine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.util.CoreUtils;
import buttle.apptpl.common.jwt.JwtRepository;

@Component
@RequiredArgsConstructor
public class CaffeineJwtRepository implements JwtRepository {

    private static final String JWT_KEY_PREFIX = "auth:";
    private final CacheManager cacheManager;

    // 캐시 인스턴스를 필드로 저장
    private Cache jwtCache;
    
    @PostConstruct
    private void initCache() {
        this.jwtCache = cacheManager.getCache(CacheType.JWT_REFRESH_TOKEN.getCacheNameWithCompany());
        if (this.jwtCache == null) {
            throw new IllegalStateException("JWT 캐시를 찾을 수 없습니다: " + CacheType.JWT_REFRESH_TOKEN.getCacheNameWithCompany());
        }
    }


    private String key(String refreshToken) {
        return JWT_KEY_PREFIX + "refresh-token:" + refreshToken;
    }

    @Override
    public void saveRefreshToken(String refreshToken, SecUser secUser) {
        String value = CoreUtils.json.toString(secUser);
        this.jwtCache.put(key(refreshToken), value);
    }

    @Override
    public Authentication checkRefreshToken(String refreshToken) {
        String value = this.jwtCache.get(key(refreshToken), String.class);
        if (CoreUtils.string.isBlank(value)) {
            return null;
        }

        SecUser secUser = CoreUtils.json.toObject(value, SecUser.class);
        if (secUser == null) {
            return null;
        }

        return getAuthentication(secUser);
    }

    @Override
    public void removeRefreshToken(String refreshToken) {
        this.jwtCache.evict(key(refreshToken));
    }

    /**
     * 캐시에서 복원한 사용자로 인증 객체를 만든다.
     *
     * <h3>[§10.99] 종전에는 권한을 <b>빈 목록</b>으로 넣었다</h3>
     *
     * <p>토큰을 파싱하는 경로({@code JwtProvider.getAuthentication})는 JWT 의 {@code auth} 클레임에서
     * 권한을 복원해 넣는데, <b>캐시가 맞은 경로는 이쪽</b>으로 와서 권한이 통째로 비었다.
     * 즉 <b>첫 요청에는 권한이 있고 그 다음부터는 없는</b> 상태였다 — 재현이 들쭉날쭉한 종류의 결함이다.</p>
     *
     * <p>화면 인가({@code FwAuthorizationManager})는 {@code SecUser.authrtCd} 를 보므로 영향이 없었지만,
     * {@code auth.getAuthorities()} 를 보는 쪽(스프링 시큐리티의 {@code hasRole}·{@code @PreAuthorize},
     * 권한으로 갈리는 화면 로직)은 <b>캐시 적중 이후 조용히 권한 없는 사용자</b>를 보게 된다.</p>
     *
     * <p>복원 순서는 {@code authrtCdList}(로그인 시 담긴 전체 목록) → 없으면 {@code authrtCd}(단일 코드) 다.
     * 둘 다 없으면 빈 목록으로 둔다 — 여기서 임의의 기본 권한을 지어내면 <b>없는 권한이 생긴다.</b></p>
     */
    private Authentication getAuthentication(SecUser secUser) {
        List<GrantedAuthority> authorityList = new ArrayList<>();

        if (secUser.getAuthrtCdList() != null && !secUser.getAuthrtCdList().isEmpty()) {
            for (String code : secUser.getAuthrtCdList()) {
                if (code != null && !code.isBlank()) {
                    authorityList.add(new SimpleGrantedAuthority(code));
                }
            }
        } else if (secUser.getAuthrtCd() != null && !secUser.getAuthrtCd().isBlank()) {
            authorityList.add(new SimpleGrantedAuthority(secUser.getAuthrtCd()));
        }

        return new UsernamePasswordAuthenticationToken(secUser, "", authorityList);
    }
} 