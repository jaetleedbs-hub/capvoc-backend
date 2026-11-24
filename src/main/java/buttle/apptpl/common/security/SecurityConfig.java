package buttle.apptpl.common.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import micoai.corebase.security.LoginRateLimitFilter;
import micoai.corebase.util.CoreUtils.string;
import buttle.apptpl.common.adapter.filter.CompanyApiAuthFilter;
import buttle.apptpl.common.jwt.JwtAuthFilter;
import buttle.apptpl.common.jwt.JwtProvider;
import buttle.apptpl.common.security.crypto.CustomPasswordEncoder;

@Slf4j
@Configurable
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final String LOGIN_URI = "/login";
	private static final String LOGOUT_URI = "/logout";
	private static final String SWAGGER_URI = "/swagger-ui/**";
	private static final String SWAGGER_RES_URI = "/swagger-resources/**";
	private static final String SWAGGER_DOCS_URI = "/v3/api-docs/**";
	private static final String HEALTH_CHECK_URI = "/api/health/**";

	/**
	 * API 문서(Swagger)를 <b>열어 둘 것인가</b>. 아래 permitAll 의 스웨거 3종은 이 값이 참일 때만 등록한다.
	 *
	 * <p>왜 프로파일이 아니라 이 값인가 — <b>설정과 보안이 어긋나지 않게</b> 하려는 것이다.
	 * 프로파일 목록으로 가르면 목록에 없는 프로파일이 생겼을 때 조용히 갈리고, 그때 어느 쪽이
	 * 참인지 알려면 두 곳을 대조해야 한다. 문서를 켜는 스위치 하나만 보게 한다.
	 *
	 * <p><b>기본값이 false 인 것이 요점이다.</b> {@code springdoc.api-docs.enabled} 는 springdoc 자체
	 * 기본이 <b>켜짐</b>이라({@code SpringDocConfiguration} 의 {@code matchIfMissing = true}),
	 * 설정을 안 주면 문서가 열린다. {@code swagger-ui.enabled: false} 는 <b>화면만</b> 끌 뿐
	 * {@code /v3/api-docs} 는 그대로 응답한다 — 둘은 별개 속성이다.
	 * 그래서 {@code application.yml} 에서 이 값을 명시적으로 false 로 내려 두고, 켤 환경에서만 켠다.
	 */
	@Value("${springdoc.api-docs.enabled:false}")
	private boolean apiDocsEnabled;

	@Value("${server.servlet.session.cookie.domain:not-defined}")
	private String cookieDomain;

	@Value("${server.servlet.session.cookie.name:MSESSION_ID}")
	private String cookieName;

	@Autowired
	private FwAuthorizationManager authorizationManager;

	@Autowired
	private SvcEnvConfig svcConfig;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Bean
	@Order(1)
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		if (svcConfig.getCsrf() == null) {
			svcConfig.setCsrf(true);
		}

		if (svcConfig.getCsrf()) {
			svcConfig.getCsrfExceptList().add(LOGIN_URI);
			svcConfig.getCsrfExceptList().add(LOGOUT_URI);
			svcConfig.getCsrfExceptList().add(SWAGGER_URI);
			svcConfig.getCsrfExceptList().add(SWAGGER_RES_URI);
			svcConfig.getCsrfExceptList().add(SWAGGER_DOCS_URI);
			svcConfig.getCsrfExceptList().add(HEALTH_CHECK_URI);
			http.csrf(csrf -> csrf.ignoringRequestMatchers(svcConfig.getCsrfExceptList().toArray(new String[0])));
			if (!string.equals(cookieDomain, "not-defined")) {
				CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
				csrfRepo.setCookieCustomizer(cookie -> cookie.domain(cookieDomain));
				http.csrf(csrf -> csrf.csrfTokenRepository(csrfRepo));
				log.info("CSRF-COOKIE-DOMAIN:" + cookieDomain);
			}
		} else {
			http.csrf(AbstractHttpConfigurer::disable);
		}

		// Logout 후에 이동할 URL 설정. 없으면, Login URI로 설정

		http.authorizeHttpRequests(requests -> {
						requests
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
						.requestMatchers(LOGIN_URI).permitAll()
                        .requestMatchers(LOGOUT_URI).permitAll();

						// 스웨거는 <켜 둔 환경에서만> 무인증으로 연다.
						// 끈 환경에서는 애초에 엔드포인트가 없으므로 여는 것 자체가 뜻이 없고,
						// 열어 두면 "문서를 껐는데 경로는 열려 있다"는 어긋남만 남는다.
						if (apiDocsEnabled) {
							requests
							.requestMatchers(SWAGGER_URI).permitAll()
							.requestMatchers(SWAGGER_RES_URI).permitAll()
							.requestMatchers(SWAGGER_DOCS_URI).permitAll();
						}

						requests
						.requestMatchers(HEALTH_CHECK_URI).permitAll()
                        .anyRequest().access(authorizationManager);
					})
			.formLogin(login -> login
				// .loginPage(svcConfig.getLoginUrl())
				.loginProcessingUrl(LOGIN_URI)
				//.failureUrl(svcConfig.getLoginUrl() + "?error=true")
				.successHandler(successHandler())
				.failureHandler(failureHandler()))
			.logout(logout -> logout
				.logoutUrl(LOGOUT_URI)
				.invalidateHttpSession(true)
				.deleteCookies(cookieName)
				.logoutSuccessUrl("/"))
			.exceptionHandling(handling -> handling
				.authenticationEntryPoint(ajaxAwareAuthenticationEntryPoint(svcConfig.getLoginUrl())))
			.cors(withDefaults())
			// [N-14] 보안 헤더 정비. CSP 는 리소스 제한(default-src 등)을 넣지 않는다(화면 깨짐 방지, 별도 트랙).
			.headers(headers -> headers
				.contentTypeOptions(withDefaults())
				.frameOptions(frame -> frame.sameOrigin())
				.httpStrictTransportSecurity(hsts -> hsts
						.includeSubDomains(true).maxAgeInSeconds(31536000))
				.referrerPolicy(rp -> rp.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
				.contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self'")));

		// [N-13] 로그인 무차별 대입 방어 — 인증 필터보다 먼저 등록해 시도 자체를 차단.
		http.addFilterBefore(loginRateLimitFilter(), UsernamePasswordAuthenticationFilter.class);
		// AICC API 인증필터를 JWT 필터 앞에 추가 (Order=1)
		http.addFilterBefore(companyApiAuthFilter(), UsernamePasswordAuthenticationFilter.class);
		// JWT 인증필터를 UsernamePasswordAuthenticationFilter 앞에 추가 (Order=2)
		http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	@Order(2)
	public WebSecurityCustomizer webSecurityCustomizer() {
		// 정적 리소스 spring security 대상에서 제외
		return (web) ->
				web.ignoring()
						.requestMatchers(
								PathRequest.toStaticResources().atCommonLocations()
						);
	}

	/**
	 * 비밀번호 인코더.
	 *
	 * <p>{@link CustomPasswordEncoder} 는 {@code @Component} 라 이미 빈으로 있다.
	 * 여기서 {@code new} 로 또 만들면 <b>두 벌</b>이 생기고, 설정(강도)을 주입받지 못한 쪽이
	 * Spring Security 에 물릴 수 있다. 그래서 만들지 않고 <b>그것을 내어 준다.</b>
	 */
	@Bean
	PasswordEncoder passwordEncoder(CustomPasswordEncoder customPasswordEncoder) {
		return customPasswordEncoder;
	}

	/**
	 * 내부 서비스 간 호출에 쓰는 사전 공유 키.
	 * 호출 측(desk-backend {@code admin-api.api-key})과 같은 값이어야 한다.
	 */
	@Value("${app.internal-api-key:}")
	private String internalApiKey;

	/** 키 불일치 처리 정책. 로컬·dev 는 AUDIT, demo 는 ENFORCE. */
	@Value("${app.internal-api-auth-policy:AUDIT}")
	private InternalApiAuthPolicy internalApiAuthPolicy;

	@Bean
	CompanyApiAuthFilter companyApiAuthFilter() {
		return new CompanyApiAuthFilter(internalApiKey, internalApiAuthPolicy);
	}

	// [N-13] 앱 레벨 로그인 rate limit. 게이트웨이 우회 경로에서도 WAS 가 스스로 방어한다.
	@Value("${security.login-rate-limit.enabled:true}")
	private boolean loginRateLimitEnabled;
	@Value("${security.login-rate-limit.max-attempts:10}")
	private int loginRateLimitMaxAttempts;
	@Value("${security.login-rate-limit.window-seconds:60}")
	private long loginRateLimitWindowSeconds;

	@Bean
	LoginRateLimitFilter loginRateLimitFilter() {
		return new LoginRateLimitFilter(
				List.of(LOGIN_URI),
				loginRateLimitMaxAttempts, loginRateLimitWindowSeconds, loginRateLimitEnabled);
	}

	@Bean
	JwtAuthFilter jwtAuthFilter() {
		return new JwtAuthFilter();
	}


	@Bean
	JwtProvider jwtProvider() {
		return new JwtProvider();
	}

	/**
	 * 로그인이 성공했을 때, 실행할 handler 설정
	 *
	 * @return
	 */
	@Bean
	AuthenticationSuccessHandler successHandler() {
		return new LoginSuccessHandler();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}


	@Bean
	UserDetailsChecker userDetailsChecker() {
		return new CustomUserDetailsChecker();
	}

	@Bean
	AuthenticationProvider authenticationProvider(CustomPasswordEncoder customPasswordEncoder) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setPostAuthenticationChecks(userDetailsChecker());
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(customPasswordEncoder);
		// 인증 성공 후 encoder.upgradeEncoding() 이 true 면 updatePassword() 로 해시를 다시 저장한다.
		//  이 배선이 없으면 upgradeEncoding 을 재정의해도 <아무 일도 일어나지 않는다>.
		//  지금은 BCrypt 하나뿐이라 늘 false 지만, 강도를 올리거나 해시를 바꿀 때 필요한 자리라 남겨 둔다.
		authProvider.setUserDetailsPasswordService(userDetailsService);

		return authProvider;
	}

	/**
	 * 로그인이 실패했을 때, 실행할 handler 설정
	 *
	 * @return
	 */
	@Bean
	AuthenticationFailureHandler failureHandler() {
		return new LoginFailureHandler();
	}

	/**
	 * 인증되지 않은 요청중 AJAX요청일 경우 401로 응답, AJAX요청이 아닐 경우 login으로 리다이렉트
	 *
	 * @param url
	 * @return
	 */
	private AjaxAwareAuthenticationEntryPoint ajaxAwareAuthenticationEntryPoint(String url) {
		return new AjaxAwareAuthenticationEntryPoint(url);
	}

	// CORS 허용 적용
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		boolean fullWildcardOrigin = false;
		for (String origin : svcConfig.getCors().getOrigins()) {
			if (string.contains(origin, "*")) {
				configuration.addAllowedOriginPattern(origin);
				if ("*".equals(origin.trim())) {
					fullWildcardOrigin = true;
				}
			} else {
				configuration.addAllowedOrigin(origin);
			}
		}

		for (String header : svcConfig.getCors().getHeaders()) {
			configuration.addExposedHeader(header);
		}

		String[] defaultExposedHeaders = {  "Authorization", "Set-RefreshToken", // 기존에 없었다면 추가
		"Content-Disposition", "Content-Language", "Content-Transfer-Encoding", 
		"Content-Type", "Set-AccessToken", "Set-RefreshTokenExpiresIn" };
		
		for (String header : defaultExposedHeaders) {
			configuration.addExposedHeader(header);
		}

		configuration.addAllowedHeader("*");
		for (String method : svcConfig.getCors().getMethods()) {
			configuration.addAllowedMethod(method);
		}
		// [WB-4] 전체 와일드카드('*') origin 과 allowCredentials 를 동시에 쓰면 Spring 이 요청 Origin 을
		// 반사하며 자격증명을 허용해, 모든 사이트에서 쿠키/토큰 포함 요청이 가능해진다(탈취·CSRF).
		// 그 조합을 차단한다 — credentials 가 필요하면 운영에서 실제 origin 화이트리스트를 설정해야 한다.
		if (fullWildcardOrigin) {
			log.warn("[WB-4] CORS origin '*' 와 allowCredentials 동시 사용 불가 — credentials 비활성. 운영은 실제 origin 화이트리스트를 설정하라.");
			configuration.setAllowCredentials(false);
		} else {
			configuration.setAllowCredentials(true);
		}

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration(svcConfig.getCors().getPattern(), configuration);
		return source;
	}
}
