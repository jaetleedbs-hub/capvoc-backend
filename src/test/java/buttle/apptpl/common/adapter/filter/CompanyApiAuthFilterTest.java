package buttle.apptpl.common.adapter.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import buttle.apptpl.common.security.InternalApiAuthPolicy;

/**
 * [§10.88] 사람 없는 내부 호출자(배치·서비스 간 신호)의 인증을 고정한다.
 *
 * <p><b>왜 필요한가.</b> 이 필터는 진입 조건이 <b>{@code X-Member-Id} 헤더 하나</b>였다.
 * 그래서 스케줄 배치처럼 사람이 없는 호출자는 {@code X-Api-Key} 를 정확히 줘도
 * <b>키를 읽지도 않고</b> 지나쳐 익명으로 처리됐다. desk 는 "키는 맞는데 주체가 안 선다"였는데
 * (§10.87 ②) 여기는 한 겹 더 나빠서 <b>키 검증 자체가 신원 헤더 안쪽</b>에 있었다.
 *
 * <p><b>dev 가 이 결함을 숨긴다.</b> {@code anonymous-mode=true} + {@code api-auth-policy=AUDIT}
 * 라 키를 주든 안 주든 200 이 나온다. demo·운영은 반대값이라 같은 호출이 401 이 된다 —
 * 화면으로 보는 사람이 없는 경로라, 고치지 않으면 통계 배치처럼 조용히 죽는다.
 * 그래서 이 테스트는 <b>{@code ENFORCE} 기준</b>으로 판정한다.
 */
class CompanyApiAuthFilterTest {

    private static final String VALID_KEY = "test-internal-key-0123456789";

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Authentication run(CompanyApiAuthFilter filter, MockHttpServletRequest request) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        // 체인 안에서 읽어야 한다 — 필터가 finally 에서 SecurityContext 를 지운다.
        Authentication[] captured = new Authentication[1];
        MockFilterChain chain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                captured[0] = SecurityContextHolder.getContext().getAuthentication();
            }
        };
        filter.doFilter(request, response, chain);
        request.setAttribute("__status", response.getStatus());
        return captured[0];
    }

    private MockHttpServletRequest get(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        request.setRequestURI(uri);
        return request;
    }

    @Test
    @DisplayName("신원 헤더가 없어도 키가 맞으면 인증된다 — 사람 없는 호출자(배치·신호)를 위해")
    void authenticatesKeyOnlyCaller() throws Exception {
        CompanyApiAuthFilter filter = new CompanyApiAuthFilter(VALID_KEY, InternalApiAuthPolicy.ENFORCE);

        //  [A5-1] 종전에는 여기서 /api/cmpy/list/members/apt001/contact 를 썼다.
        //  그 경로는 회원사 임직원 연락처를 돌려주는데 <허용 목록에 없다> — 키 하나로 열려 있던
        //  상태를 이 테스트가 그대로 굳히고 있었던 셈이다. 실제 S2S 경로로 바꾼다.
        MockHttpServletRequest request = get("/api/cmpy/type-cd/cmpy-type-code");
        request.addHeader("X-Api-Key", VALID_KEY);

        Authentication auth = run(filter, request);

        assertThat(auth).as("키가 맞으면 신원 헤더가 없어도 인증 주체가 서야 한다").isNotNull();
        assertThat(auth.getAuthorities().stream().map(GrantedAuthority::getAuthority))
                .as("사람 없는 호출자에게 관리자 권한을 주지 않는다 — 서비스 표식만")
                .containsExactly("ROLE_INTERNAL_S2S");
        assertThat(request.getAttribute(CompanyApiAuthFilter.ATTR_INTERNAL_S2S))
                .as("키 검증을 통과한 호출만 내부 표식을 받는다").isEqualTo(Boolean.TRUE);
    }

    @Test
    @DisplayName("키가 틀리면 ENFORCE 에서 차단된다 — 위 완화가 게이트를 열어선 안 된다")
    void rejectsWrongKeyWithoutIdentity() throws Exception {
        CompanyApiAuthFilter filter = new CompanyApiAuthFilter(VALID_KEY, InternalApiAuthPolicy.ENFORCE);

        MockHttpServletRequest request = get("/api/cmpy/list/members/apt001/contact");
        request.addHeader("X-Api-Key", "wrong-key");

        Authentication auth = run(filter, request);

        assertThat(auth).as("키가 틀리면 인증이 서면 안 된다").isNull();
        assertThat(request.getAttribute("__status")).isEqualTo(401);
    }

    @Test
    @DisplayName("서버 키가 비어 있으면 통과시키지 않는다 — 설정 누락이 무인증 개방이 되면 안 된다")
    void rejectsWhenServerKeyMissing() throws Exception {
        CompanyApiAuthFilter filter = new CompanyApiAuthFilter("", InternalApiAuthPolicy.ENFORCE);

        MockHttpServletRequest request = get("/api/cmpy/list/members/apt001/contact");
        request.addHeader("X-Api-Key", VALID_KEY);

        Authentication auth = run(filter, request);

        assertThat(auth).isNull();
        assertThat(request.getAttribute("__status")).isEqualTo(401);
    }

    @Test
    @DisplayName("[§10.90] AUDIT 에서 키 없는 신원 헤더는 권한을 못 받는다 — 헤더 한 줄로 관리자가 되면 안 된다")
    void auditDoesNotGrantAdminToForgedIdentity() throws Exception {
        // AUDIT 는 <막지 않는> 정책이지 <권한을 주는> 정책이 아니다.
        // 종전에는 여기서 ROLE_USER+ROLE_ADMIN 이 붙어, dev(anonymous-mode+AUDIT)에서는
        // X-Member-Id 한 줄만으로 관리자 권한이 됐다.
        CompanyApiAuthFilter filter = new CompanyApiAuthFilter(VALID_KEY, InternalApiAuthPolicy.AUDIT);

        MockHttpServletRequest request = get("/api/cmpy/list/members/apt001/contact");
        request.addHeader("X-Member-Id", "attacker");
        request.addHeader("X-Login-Id", "attacker");

        Authentication auth = run(filter, request);

        assertThat(auth).as("키 없는 신원 헤더에 인증 주체를 세우면 안 된다").isNull();
        assertThat(request.getAttribute(CompanyApiAuthFilter.ATTR_INTERNAL_S2S))
                .as("내부 표식도 남기면 안 된다").isNull();
        assertThat(request.getAttribute("__status"))
                .as("AUDIT 이므로 요청 자체는 막지 않는다 — 기존 동작 유지").isEqualTo(200);
    }

    @Test
    @DisplayName("[§10.90] 키가 맞으면 종전대로 관리자 권한을 받는다 — 정상 호출자가 깨지지 않는다")
    void trustedIdentityStillGetsAdmin() throws Exception {
        CompanyApiAuthFilter filter = new CompanyApiAuthFilter(VALID_KEY, InternalApiAuthPolicy.AUDIT);

        MockHttpServletRequest request = get("/api/cmpy/list/members/apt001/contact");
        request.addHeader("X-Api-Key", VALID_KEY);
        request.addHeader("X-Member-Id", "demo01");

        Authentication auth = run(filter, request);

        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities().stream().map(GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        assertThat(request.getAttribute(CompanyApiAuthFilter.ATTR_INTERNAL_S2S)).isEqualTo(Boolean.TRUE);
    }

    @Test
    @DisplayName("키도 신원도 없으면 이 필터는 손대지 않는다 — 브라우저 요청은 뒤의 인증이 판단한다")
    void leavesPlainRequestAlone() throws Exception {
        CompanyApiAuthFilter filter = new CompanyApiAuthFilter(VALID_KEY, InternalApiAuthPolicy.ENFORCE);

        MockHttpServletRequest request = get("/api/cmpy/list/members/apt001/contact");

        Authentication auth = run(filter, request);

        assertThat(auth).as("이 필터가 브라우저 요청에 주체를 세우면 안 된다").isNull();
        assertThat(request.getAttribute("__status"))
                .as("차단도 이 필터의 몫이 아니다 — 뒤의 인증·인가가 판단한다").isEqualTo(200);
    }

    @Test
    @DisplayName("[A5-1] 목록에 없는 경로는 키가 맞아도 S2S 주체가 서지 않는다 — 공유키는 만능키가 아니다")
    void keyOnlyDoesNotOpenUnlistedPaths() throws Exception {
        CompanyApiAuthFilter filter = new CompanyApiAuthFilter(VALID_KEY, InternalApiAuthPolicy.ENFORCE);

        //  실측(dev 2026-08-20): 이 경로가 키 없이 302, 키만 주면 200 이었다.
        //  화면 인가(A3-1)는 ENFORCE 로 잠갔는데 S2S 경로만 열려 있었다.
        MockHttpServletRequest request = get("/api/log/batch/jobs");
        request.addHeader("X-Api-Key", VALID_KEY);

        Authentication auth = run(filter, request);

        assertThat(auth).as("목록에 없으면 키가 맞아도 주체를 세우지 않는다").isNull();
        assertThat(request.getAttribute(CompanyApiAuthFilter.ATTR_INTERNAL_S2S))
                .as("인가 우회 표식도 주지 않는다 — 이 표식이 곧 만능키였다").isNull();
    }

    @Test
    @DisplayName("[A5-1] 형제 경로가 함께 열리지 않는다 — 프리픽스가 아니라 정확 경로다")
    void keyOnlySiblingPathStaysClosed() throws Exception {
        CompanyApiAuthFilter filter = new CompanyApiAuthFilter(VALID_KEY, InternalApiAuthPolicy.ENFORCE);

        //  /api/cmpy/type-cd/cmpy-type-code 는 열되, /api/cmpy/ 를 프리픽스로 열면
        //  회원사 임직원 연락처(list/members/**)까지 딸려 온다.
        MockHttpServletRequest request = get("/api/cmpy/list/members/apt001/contact");
        request.addHeader("X-Api-Key", VALID_KEY);

        Authentication auth = run(filter, request);

        assertThat(auth).as("형제 경로는 열리지 않아야 한다").isNull();
    }

    @Test
    @DisplayName("[A5-1] 신원 헤더가 있는 호출은 이 목록의 영향을 받지 않는다 — 다른 분기다")
    void identityCallerUnaffectedByPathList() throws Exception {
        CompanyApiAuthFilter filter = new CompanyApiAuthFilter(VALID_KEY, InternalApiAuthPolicy.ENFORCE);

        //  /api/admin/polling/all 은 목록에 없지만 신원 헤더를 함께 보내는 실사용 경로다.
        //  경로 제한을 키-only 분기에만 걸었으므로 여기는 종전대로 동작해야 한다.
        MockHttpServletRequest request = get("/api/admin/polling/all");
        request.addHeader("X-Api-Key", VALID_KEY);
        request.addHeader("X-Member-Id", "probe");

        Authentication auth = run(filter, request);

        assertThat(auth).as("신원 헤더 분기는 경로 목록과 무관하다").isNotNull();
    }
}
