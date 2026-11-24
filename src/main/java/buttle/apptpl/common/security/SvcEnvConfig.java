package buttle.apptpl.common.security;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.ToString;

@Data
@Component
@ConfigurationProperties("svc-env")
public class SvcEnvConfig {
	
	Cors cors;
	String loginUrl;
	private Boolean csrf;
	private List<String> csrfExceptList;
	private String initialPswd;
	private Integer initialPswdDays;
	private Long sessionTimeout;
	/**
	 * @deprecated {@link #apiAuthPolicy} 로 대체. 값이 있으면 호환 해석된다
	 *             (true→ENFORCE, false→OFF). {@link ApiAuthPolicy#resolve}
	 */
	@Deprecated
	private Boolean apiAuthentication;

	/** API 권한 검사 정책(OFF/AUDIT/ENFORCE). 미설정 시 {@link #apiAuthentication} 로 호환 해석한다. */
	private ApiAuthPolicy apiAuthPolicy;
	private LoginAttempt loginAttempt;
	private JwtConfig jwt;

	@Data
	public static class Cors {
		List<String> origins;
		List<String> headers;
		List<String> methods;
		String pattern;
	}

	@Data
	@ToString
	public static class LoginAttempt {

		// 비밀번호 변경 기준 일수
		private Integer initialPswdDays;

		// 중복로그인 체크 시간
		private Integer duplicateLoginTime;

		// 중복로그인 허용 여부
		private Boolean allowDuplicationLogin;

		// 로그인 실패 횟수
		private Integer loginFailCount;
	}

	@Data
	public static class JwtConfig {
		private String secret;
		private Long accessTokenValidityInSeconds;
		private Long refreshTokenValidityInSeconds;
		private Boolean anonymousMode;
		private Boolean cookieSecure;
	}


}
