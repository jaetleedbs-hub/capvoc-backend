package buttle.apptpl.common.adapter.filter;

import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.vo.SecUser;
import buttle.apptpl.common.security.InternalApiPaths;
import buttle.apptpl.common.security.InternalApiAuthPolicy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * AICC API 인증 필터
 *
 * <p>AICC(desk-backend)에서 호출하는 서버 간 API 요청의 사용자 정보를 처리합니다.</p>
 * <ul>
 *   <li>사전 공유 키({@code X-Api-Key}) 검증으로 <b>내부 서비스 호출인지 먼저 확인</b></li>
 *   <li>통과한 요청에 한해 커스텀 헤더(X-Member-Id 등)에서 사용자 정보 추출</li>
 *   <li>SecUser 객체 생성 및 SecurityContext 설정</li>
 *   <li>JWT 필터보다 먼저 실행되어야 함</li>
 * </ul>
 *
 * <h3>왜 키 검증이 필요한가</h3>
 * 종전에는 {@code X-Member-Id} 헤더가 있기만 하면 그 값을 그대로 신뢰해 인증 주체를 만들고
 * {@code ROLE_USER}·{@code ROLE_ADMIN}을 부여했다. 뒤이은 {@code JwtAuthFilter}는
 * "이미 인증됨"으로 보고 검사를 건너뛰므로, <b>포트에 도달할 수 있는 누구나 헤더 하나로
 * 관리자 권한을 얻을 수 있었다</b>. desk-backend 는 같은 결함을 W0-4 에서
 * {@code AdminApiAuthFilter} 의 공유키 검증으로 막았고, 이 필터는 그 구조를 admin 측에 맞춘 것이다.
 *
 * <h3>인가 계층과의 관계</h3>
 * 키 검증을 통과한 요청에는 {@link #ATTR_INTERNAL_S2S} 요청 속성을 남긴다.
 * {@code FwAuthorizationManager} 는 이 표식을 보고 <b>프로그램-권한 매핑 검사를 우회</b>한다 —
 * S2S 호출자는 관리자 화면 권한(authrt_cd)을 갖지 않으므로 매핑 검사를 적용하면 전부 차단된다.
 * 즉 S2S 의 게이트는 공유키이고, 사람 사용자의 게이트는 프로그램-권한 매핑이다.
 *
 * @since 2026-01-19
 * @author ID-ABO 개발팀
 */
@Slf4j
public class CompanyApiAuthFilter extends OncePerRequestFilter {

    private static final String HEADER_MEMBER_ID = "X-Member-Id";
    private static final String HEADER_LOGIN_ID = "X-Login-Id";
    private static final String HEADER_MEMBER_NAME = "X-Member-Name";
    private static final String HEADER_COMPANY_ID = "X-Company-Id";

    /** 호출 측(desk {@code AdminApiAdapter})이 보내는 사전 공유 키 헤더. */
    private static final String HEADER_API_KEY = "X-Api-Key";

    /** [§10.88] 신원 헤더 없이 키만 제시한 호출자(배치·서비스 간 신호)에 부여할 식별자. */
    private static final String S2S_USER_ID = "s2s";

    /** 위 S2S 주체의 권한. 업무 권한이 아니라 "키로 인증된 내부 호출"이라는 표식이다. */
    private static final String S2S_AUTHORITY = "ROLE_INTERNAL_S2S";

    /**
     * 이 요청이 키 검증을 통과한 내부 서비스 호출임을 표시하는 요청 속성.
     * {@code FwAuthorizationManager} 가 읽는다.
     */
    public static final String ATTR_INTERNAL_S2S = CompanyApiAuthFilter.class.getName() + ".INTERNAL_S2S";

    /** 설정된 사전 공유 키. 비어 있으면 신뢰를 수립할 수 없는 상태다. */
    private final String expectedApiKey;

    /** 키 불일치 시 차단할지(ENFORCE), 기록만 할지(AUDIT). */
    private final InternalApiAuthPolicy policy;

    public CompanyApiAuthFilter(String expectedApiKey, InternalApiAuthPolicy policy) {
        this.expectedApiKey = expectedApiKey;
        this.policy = policy == null ? InternalApiAuthPolicy.AUDIT : policy;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // AICC에서 호출한 요청인지 확인 (X-Member-Id 헤더 존재 여부)
        String memberId = request.getHeader(HEADER_MEMBER_ID);
        boolean hasIdentity = memberId != null && !memberId.isEmpty();

        // [§10.88] 키만 제시한 호출도 이 필터가 처리한다.
        //
        //  종전에는 진입 조건이 <신원 헤더 하나>였다. 그래서 사람이 없는 호출자(스케줄 배치·
        //  서비스 간 신호)는 X-Api-Key 를 정확히 줘도 <키를 읽지도 않고> 그냥 지나쳤고,
        //  뒤에서 익명으로 처리됐다. desk 는 "키는 맞는데 주체가 안 선다"였는데(§10.87 ②)
        //  admin 은 한 겹 더 나빠서 <키 검증 자체가 신원 헤더 안쪽에> 있었다.
        //
        //  dev 가 이 결함을 숨긴다 — anonymous-mode=true + api-auth-policy=AUDIT 라
        //  키를 주든 안 주든 200 이 나온다. demo·운영은 반대값이라 같은 호출이 401 이 된다.
        //  화면으로 보는 사람이 없는 경로라, 고치지 않으면 §10.87 과 똑같이 조용히 죽는다.
        String providedKey = request.getHeader(HEADER_API_KEY);
        boolean hasKey = providedKey != null && !providedKey.isEmpty();
        boolean isAiccRequest = hasIdentity || hasKey;

        if (hasIdentity) {
            log.debug("AICC API 요청 감지 - memberId: {}, URI: {}", memberId, request.getRequestURI());

            // 신원 헤더를 신뢰하기 전에 호출자가 내부 서비스인지부터 확인한다.
            // 이 확인이 없으면 헤더 조작만으로 임의 사용자·관리자 권한 인증이 가능하다.
            boolean trusted = isTrustedCaller(request);
            if (!trusted) {
                if (policy.isEnforcing()) {
                    reject(response);
                    return;
                }
                // [§10.90] AUDIT: 요청은 막지 않되 <권한은 주지 않는다>.
                //
                //  종전에는 여기서 그대로 내려가 아래 블록이 ROLE_USER+ROLE_ADMIN 을 붙였다.
                //  즉 AUDIT 환경에서는 <X-Member-Id 헤더 한 줄만으로 관리자 권한>이 됐다.
                //  dev 가 바로 그 상태다(anonymous-mode=true + api-auth-policy=AUDIT).
                //
                //  요청 자체를 막지 않는 것이 AUDIT 의 취지이므로 통과는 시킨다. 다만 주체를
                //  세우지 않으면 익명이 되고, AUDIT 인가 관리자는 익명도 통과시키므로
                //  <기존 동작은 그대로>다 — 잃는 것은 위조로 얻던 관리자 권한뿐이다.
                //  ENFORCE 에서는 위에서 이미 401 이라 이 경로에 오지 않는다.
                log.warn("키 검증에 실패한 신원 헤더 — 권한을 부여하지 않고 통과시킨다(AUDIT). "
                        + "memberId={}, URI={}", memberId, request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            try {
                // ===== 중요: 기존 인증 정보 초기화 후 AICC 사용자로 설정 =====
                // AICC API 요청은 항상 헤더의 사용자 정보를 우선 사용
                SecurityContextHolder.clearContext();

                // 헤더에서 사용자 정보 추출
                String loginId = request.getHeader(HEADER_LOGIN_ID);
                String memberName = request.getHeader(HEADER_MEMBER_NAME);
                String companyId = request.getHeader(HEADER_COMPANY_ID);

                // SecUser 객체 생성 (Admin Backend 형식)
                SecUser user = SecUser.builder()
                        .userId(memberId)
                        .lgnId(loginId != null ? loginId : memberId)
                        .userNm(memberName != null ? memberName : "AICC User")
                        .enabled(true)
                        .build();

                // [§10.90] 여기까지 온 호출은 <키 검증을 통과한> 것뿐이다.
                //  권한은 호출자가 선언하지 않는다 — 서버가 정한 고정값이고, 게이트는 키다.
                //  (키 없이 신원 헤더만 보낸 호출은 위에서 권한 없이 통과시켰다.)
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

                // Authentication 객체 생성 및 SecurityContext 설정
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 키 검증을 통과한 호출만 인가 계층 우회 표식을 받는다.
                request.setAttribute(ATTR_INTERNAL_S2S, Boolean.TRUE);

                log.info("AICC 사용자 인증 성공 - memberId: {}, loginId: {}, URI: {}, 키검증: {}",
                        memberId, loginId, request.getRequestURI(), trusted);

            } catch (Exception e) {
                log.error("AICC 사용자 정보 처리 중 오류 - memberId: {}", memberId, e);
            }
        } else if (hasKey) {
            // [§10.88] 신원 헤더 없이 키만 제시한 내부 호출(사람이 없는 호출자).
            //  키를 <먼저> 검증한다 — 종전에는 이 경로가 검증조차 없이 지나갔다.
            //
            // [A5-1 §1.4-1] 그런데 §10.88 에는 <경로 제한이 없었다>. 키만 맞으면 어떤 admin API 든
            //  ATTR_INTERNAL_S2S 를 받고 인가 검사를 통째로 우회했다 — 공유키가 만능키였다.
            //  실측(dev): /api/log/batch/jobs 가 키 없이 302, 키만 주면 200.
            //  이제 InternalApiPaths 에 등록된 경로에만 S2S 주체를 세운다.
            //  ★ 순서가 중요하다. 키 검증이 <먼저>다.
            //   처음에 경로 판정을 앞에 두고 목록 밖이면 흘려보내게 했더니,
            //   <틀린 키·서버키 미설정에도 401 이 아니라 200> 이 됐다 — 기존 보장이 사라졌다.
            //   (CompanyApiAuthFilterTest 의 rejectsWrongKeyWithoutIdentity·rejectsWhenServerKeyMissing 이 잡았다.)
            //   키는 경로와 무관하게 항상 검증하고, 경로 목록은 <주체를 세울지>만 가른다.
            boolean trusted = isTrustedCaller(request);
            if (!trusted) {
                if (policy.isEnforcing()) {
                    reject(response);
                    return;
                }
                // AUDIT: 기록만 하고 통과시킨다(전환 기간). 주체는 세우지 않는다.
            } else if (!InternalApiPaths.isKeyOnlyAllowed(request.getRequestURI())) {
                // [A5-1 §1.4-1] 키는 맞지만 <목록에 없는 경로>다.
                //  §10.88 에는 경로 제한이 없어, 키만 맞으면 어떤 admin API 든 ATTR_INTERNAL_S2S 를
                //  받고 인가를 통째로 우회했다 — 공유키가 만능키였다.
                //  실측(dev 2026-08-20): /api/log/batch/jobs 가 키 없이 302, 키만 주면 200.
                //
                //  여기서 <차단하지 않고> 주체만 세우지 않는다. 못 찾은 호출자가 있으면
                //  즉시 죽기 때문이다(§10.87 이 넉 달을 잃은 방식). 주체가 없으면 뒤의 인가 계층이
                //  판단한다 — 사람 경로면 로그인 요구, 그 외는 거부.
                //  이 경고에 쌓인 경로가 곧 등록 후보다. 0 이 되면 목록이 맞다는 뜻이다.
                log.warn("[A5-1] 키는 맞으나 목록에 없는 경로 — S2S 주체를 세우지 않는다. URI: {}, remote: {}",
                        request.getRequestURI(), request.getRemoteAddr());
            } else {
                SecurityContextHolder.clearContext();
                //  권한은 <서비스 표식 하나>만 준다. 위 신원 분기가 ROLE_ADMIN 까지 주는 것과
                //  일부러 다르게 둔다 — 사람 없는 호출자에게 관리자 권한을 줄 이유가 없고,
                //  필요해지면 그때 <경로별로> 부여해야 한다(desk 의 레인 방식).
                SecUser s2sUser = SecUser.builder()
                        .userId(S2S_USER_ID)
                        .lgnId(S2S_USER_ID)
                        .userNm(S2S_USER_ID)
                        .enabled(true)
                        .build();
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(s2sUser, null,
                                List.of(new SimpleGrantedAuthority(S2S_AUTHORITY))));
                request.setAttribute(ATTR_INTERNAL_S2S, Boolean.TRUE);
                log.info("S2S 인증 성공(신원 헤더 없음) - URI: {}, 권한: {}",
                        request.getRequestURI(), S2S_AUTHORITY);
            }
        }

        try {
            // 다음 필터로 진행
            filterChain.doFilter(request, response);
        } finally {
            // ===== 중요: 요청 처리 완료 후 SecurityContext 정리 =====
            // AICC API 요청으로 설정된 인증 정보를 정리하여 다음 요청에 영향 없도록 함
            if (isAiccRequest) {
                SecurityContextHolder.clearContext();
                log.debug("AICC API 요청 완료 - SecurityContext 정리, memberId: {}", memberId);
            }
        }
    }

    /**
     * 호출자가 신뢰할 수 있는 내부 서비스인지 확인한다.
     *
     * <p>키가 설정되지 않은 경우는 <b>신뢰를 수립할 수 없는 상태</b>이므로 통과시키지 않는다
     * (설정 누락이 곧 무인증 개방이 되지 않도록 한다).
     */
    private boolean isTrustedCaller(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        if (expectedApiKey == null || expectedApiKey.isBlank()) {
            log.error("내부 API 키(app.internal-api-key)가 설정되지 않았습니다. 정책={}, URI={}",
                    policy, requestURI);
            return false;
        }

        String provided = request.getHeader(HEADER_API_KEY);
        if (provided == null || !constantTimeEquals(expectedApiKey, provided)) {
            log.warn("내부 API 인증에 실패했습니다. 정책={}, URI={}, remote={}, 키제시={}",
                    policy, requestURI, request.getRemoteAddr(), provided != null);
            return false;
        }

        return true;
    }

    /** 타이밍 공격을 피하기 위해 길이·내용에 무관하게 일정 시간으로 비교한다. */
    private boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    private void reject(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"error\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}");
    }
}
