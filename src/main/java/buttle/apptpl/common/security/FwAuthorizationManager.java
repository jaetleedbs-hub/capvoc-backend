package buttle.apptpl.common.security;

import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.adapter.filter.CompanyApiAuthFilter;
import buttle.apptpl.common.framework.common.vo.CustomUserDetails;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.exception.UnauthorizedException;
import buttle.apptpl.api.common.components.service.CommonComponentService;

@Slf4j
@Component
@RequiredArgsConstructor
public class FwAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {


    private final SvcEnvConfig svcEnvConfig;

    private final CommonComponentService commonComponentService;

    private final static String API_AUTHENTICATION_EXCEPTION_MESSAGE = "권한이 없습니다. ";

    /** [§10.98] 경로변수 등록을 위한 Ant 패턴 매처(`/api/x/**`). 상태가 없어 공유해도 안전하다. */
    private final static org.springframework.util.AntPathMatcher PATH_MATCHER =
            new org.springframework.util.AntPathMatcher();
    
    /**
     * API 권한 검사
     * @param authentication
     * @param requestURI
     */
    public void checkApiAuthentication(SecUser secUser, String requestURI) {

        String authrtCd = secUser.getAuthrtCd();
        if( StringUtils.isEmpty(authrtCd)) {
            throw new UnauthorizedException(API_AUTHENTICATION_EXCEPTION_MESSAGE);
        }

        // 공통 프로그램 목록 조회
        // 공통 프로그램에 존재하면 권한 검사를 하지 않는다.
        List<String> commonPrgrmList = commonComponentService.getCommonPrgrmList();
        if( matches(commonPrgrmList, requestURI)) {
            return;
        }

        // 사용자 예외 프로그램 목록 조회
        List<String> userExceptPrgrmList = commonComponentService.getUserExceptPrgrmList(secUser.getUserId());
        if( matches(userExceptPrgrmList, requestURI)) {
            throw new UnauthorizedException(API_AUTHENTICATION_EXCEPTION_MESSAGE);
        }

        // 사용자 권한 프로그램 목록 조회
        List<String> authrtPrgrmList = commonComponentService.getAuthrtPrgrmList(authrtCd);
        if( matches(authrtPrgrmList, requestURI)) {
            return;
        }

        // 사용자 확장 프로그램 목록 조회
        // 사용자 확장 프로그램에 존재하면 권한 검사를 하지 않는다.
        List<String> userExpansionPrgrmList = commonComponentService.getUserExpansionPrgrmList(secUser.getUserId());
        if( matches(userExpansionPrgrmList, requestURI)) {
            return;
        }
        throw new UnauthorizedException(API_AUTHENTICATION_EXCEPTION_MESSAGE);
    }

    /**
     * [§10.98] 등록 목록과 요청 URI 를 맞춰 본다. <b>완전일치 + 패턴</b>.
     *
     * <p><b>왜 완전일치만으로는 안 되나.</b> 현행 엔드포인트 245개 중 <b>30개가 경로변수</b>를 쓴다
     * ({@code /api/common/files/{id}} 등). 문자열이 요청마다 달라지므로 <b>등록해 두어도 영원히
     * 맞지 않는다</b> — 그 30개는 정책을 ENFORCE 로 올리는 순간 전부 403 이 된다.</p>
     *
     * <p>먼저 완전일치를 본다. 등록의 대부분이 고정 경로이고, 그쪽이 훨씬 싸고 오해의 여지가 없다.
     * 패턴은 <b>{@code *} 나 {@code ?} 가 들어 있는 항목에만</b> 적용한다 — 모든 항목을
     * 패턴으로 돌리면 고정 경로에 우연히 들어간 특수문자가 의도치 않은 범위를 열 수 있다.</p>
     *
     * <p>패턴은 {@code prgrm_id} 자체에 적는다(예: {@code /api/common/files/**}).
     * {@code url_addr_ptrn} 컬럼이 그 용도로 만들어져 있으나 <b>381행 전부 비어 있었고 인가 조회가
     * 읽지도 않았다</b> — 매퍼까지 바꾸는 대신, 등록 값 자체를 패턴으로 두어 같은 목적을 이룬다.</p>
     */
    private boolean matches(List<String> registered, String requestURI) {
        if (registered == null || registered.isEmpty() || requestURI == null) {
            return false;
        }
        if (registered.contains(requestURI)) {
            return true;
        }
        for (String entry : registered) {
            if (entry == null || (entry.indexOf('*') < 0 && entry.indexOf('?') < 0)) {
                continue;   // 고정 경로는 위 contains 로 이미 판정했다
            }
            if (PATH_MATCHER.match(entry, requestURI)) {
                return true;
            }
        }
        return false;
    }



    
    @Override
    @SuppressWarnings("deprecation") // 구 api-authentication 호환 해석을 위해 의도적으로 읽는다.
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {

        ApiAuthPolicy policy = ApiAuthPolicy.resolve(
                svcEnvConfig.getApiAuthPolicy(), svcEnvConfig.getApiAuthentication());

        // API 권한 검사 비활성화 인 경우 처리하지 않는다.
        if (policy == ApiAuthPolicy.OFF) {
            return new AuthorizationDecision(true);
        }

        // API 권한 검사 예외 URL 목록
        List<String> excludedUrls = List.of(
            "/login"
            , "/error"
            , "/jsonView"
            , "/redirect"

            , "/api/auth/"
            , "/api/common/polling/"
            , "/api/common/user-auth" // 중복로그인 체크 外
            , "/api/com0020/u01" // 비밀번호 변경
            // [§10.89] "/api/external/" 을 뺐다 — JwtAuthFilter 의 같은 항목과 한 벌이다.
            //  둘 다 있어야 "인증도 인가도 없는" 상태가 되므로 <함께> 지운다.

            , "/api-docs"
            , "/swagger-ui/"
            , "/favicon.ico"

            // [A3-1] 헬스 프로브는 <화면 인가>를 요구하면 안 된다. 익명으로 들어오므로
            //  ENFORCE 에서는 403 이 되고, 그때 배포·모니터링이 앱을 <죽은 것>으로 읽는다.
            //  이 경로는 살려 두려고 mail 헬스체크까지 꺼 둔 곳이다
            //  (config-hub/admin-backend/application-dev.yml: management.health.mail.enabled=false).
            //  노출 범위는 management.endpoints 설정이 따로 통제하며, 여기서 여는 것은
            //  <인가 검사 면제>일 뿐이다. /actuator 전체가 아니라 health 만 뺀다.
            , "/actuator/health"
        );
        
        String requestURI = context.getRequest().getRequestURI();
        // 현재 요청 URI가 예외 처리 목록에 포함되어 있는지 확인
        for (String url : excludedUrls) {
            if (requestURI.startsWith(url)) {
                return new AuthorizationDecision(true);
            }
        }

        // 사전 공유 키 검증을 통과한 내부 서비스 호출(desk→admin)은 프로그램-권한 매핑 검사를 우회한다.
        // S2S 호출자는 관리자 화면 권한(authrt_cd)을 갖지 않으므로 매핑 검사를 적용하면 전부 차단된다.
        // 즉 S2S 의 게이트는 공유키(CompanyApiAuthFilter)이고, 사람 사용자의 게이트가 이 매핑 검사다.
        if (Boolean.TRUE.equals(context.getRequest().getAttribute(CompanyApiAuthFilter.ATTR_INTERNAL_S2S))) {
            return new AuthorizationDecision(true);
        }

        Authentication auth = authentication.get();
        Object principalObj = auth.getPrincipal();
        if (principalObj == null) {
            return new AuthorizationDecision(false);
        }

        // anonymous 인 경우.
        // 종전에는 무조건 통과였다 — anonymous-mode 를 켜 둔 환경에서는 토큰 없는 요청도
        // 익명 주체로 여기까지 도달하므로, 그 통과가 곧 무인증 개방이었다.
        // [§10.98] 주체는 두 형태로 들어온다.
        //
        //  ① SecUser            — CompanyApiAuthFilter(S2S·신원헤더)가 넣는 형태
        //  ② CustomUserDetails  — <사람이 로그인한> 경로가 넣는 형태. SecUser 를 품고 있다.
        //
        //  종전에는 ①만 인정했다. 그래서 <정상 로그인한 관리자>가 전부 "익명 주체" 로 갈라져
        //  아래 매핑 검사에 도달하지 못했다 — 즉 화면 인가가 한 번도 동작한 적이 없다.
        //  (2026-08-18 dev 실측: demo01 · HTTP 200 · principal=CustomUserDetails · authorities=[PROJECT])
        //
        //  이 결함은 정책값과 무관하다. AUDIT 이라 통과해 왔을 뿐, ENFORCE 로 올렸다면
        //  <전 화면 403> 이 됐을 것이다. "ENFORCE 하면 콘솔이 다 막힌다"의 진짜 이유가 이것이다.
        SecUser secUser = resolveSecUser(principalObj);
        if (secUser == null) {
            // 진짜 익명(토큰 없는 요청)이거나, 우리가 모르는 주체 형태다.
            //  어느 쪽인지 사람이 알 수 있게 실제 클래스를 남긴다 — 이번에 이걸 안 찍어서 두 번 헛짚었다.
            log.warn("[API-AUTHZ] 인식할 수 없는 주체 — auth={}, principal={}, name={}, authorities={}, uri={}",
                    auth.getClass().getName(),
                    principalObj.getClass().getName(),
                    auth.getName(),
                    auth.getAuthorities(),
                    requestURI);
            return decide(policy, false,
                    "익명 주체의 관리자 API 접근", requestURI, String.valueOf(auth.getName()));
        }
        try {
            checkApiAuthentication(secUser, requestURI);
        } catch (UnauthorizedException e) {
            return decide(policy, false, "권한 매핑에 없는 API 접근", requestURI, secUser.getUserId());
        }
        return new AuthorizationDecision(true);
    }

    /**
     * 주체에서 {@link SecUser} 를 꺼낸다. 모르는 형태면 {@code null}.
     *
     * <p>{@code CustomUserDetails} 는 {@code SecUser} 를 <b>품고 있는</b> 로그인 주체다.
     * 품은 값이 비어 있으면 권한코드를 얻을 수 없으므로 {@code null} 로 본다 —
     * 여기서 억지로 통과시키면 권한 없는 주체가 인가를 지나간다.</p>
     */
    private SecUser resolveSecUser(Object principal) {
        if (principal instanceof SecUser secUser) {
            return secUser;
        }
        if (principal instanceof CustomUserDetails details) {
            return details.getSecUser();
        }
        return null;
    }

    /**
     * 거부 사유가 확인된 요청을 정책에 따라 처리한다.
     *
     * <p>AUDIT 은 통과시키되 경고를 남긴다 — 이 로그에 쌓인 URI 가 곧 ENFORCE 전환 전에
     * 프로그램-권한 매핑에 등록해야 할 목록이다.
     */
    private AuthorizationDecision decide(ApiAuthPolicy policy, boolean allowed,
                                         String reason, String requestURI, String userId) {
        if (allowed) {
            return new AuthorizationDecision(true);
        }
        if (policy.isEnforcing()) {
            log.warn("[API-AUTHZ][DENY] {} - uri={}, user={}", reason, requestURI, userId);
            return new AuthorizationDecision(false);
        }
        log.warn("[API-AUTHZ][AUDIT] {} - 통과시켰습니다. 매핑 등록이 필요합니다. uri={}, user={}",
                reason, requestURI, userId);
        return new AuthorizationDecision(true);
    }
}
