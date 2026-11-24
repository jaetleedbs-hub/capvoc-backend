package buttle.apptpl.common.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.util.CoreUtils;
import buttle.apptpl.common.exception.CustomAuthenticationException;
import buttle.apptpl.common.exception.vo.ErrorResponse;
import buttle.apptpl.common.security.bruteforce.LoginAttemptUtils;

@Component
@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private LoginAttemptUtils loginAttemptService;

	/**
	 * 로그인 실패 시 응답 처리
	 * @param response
	 * @param errorMessage
	 * @throws IOException
	 */
	private void writeResponse(HttpServletResponse response, String errorMessage, String errorCode) throws IOException {
		response.setStatus(400);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
		CoreUtils.json.writeValue(response.getWriter(), ErrorResponse.builder()
			.error(errorCode)
			.message(errorMessage)
			.status(400)
			.errors(new ArrayList<>())
			.build());
	}


	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

		// 사용자 존재 여부 체크
		if (exception instanceof UsernameNotFoundException) {
			// CustomUserDetailsService에서 전달된 메시지 사용
			writeResponse(response, "존재하지 않는 사용자입니다", "USER_NOT_FOUND");
			return;
		} else if (exception instanceof CustomAuthenticationException ) {
			writeResponse(response, ((CustomAuthenticationException) exception).getMessage(), ((CustomAuthenticationException) exception).getErrorCode());
			return;
		} else if(exception instanceof BadCredentialsException) {
			String userId = request.getParameter("username");
			String ip = request.getRemoteAddr();
			String message = loginAttemptService.loginFailed(userId, ip);
			writeResponse(response, message, "BAD_CREDENTIALS");
			return;
		} else if(exception instanceof AccountExpiredException) {
			writeResponse(response, "계정만료 사용자입니다", "ACCOUNT_EXPIRED");
			return;
		} else if(exception instanceof CredentialsExpiredException) {
			writeResponse(response, "비밀번호 변경일자가 도래했습니다", "CREDENTIALS_EXPIRED");
			return;
		} else if (exception.getCause() instanceof CustomAuthenticationException) {
			writeResponse(response, exception.getMessage(), ((CustomAuthenticationException) exception.getCause()).getErrorCode());
			return;
		} else if( exception instanceof LockedException) {
			writeResponse(response, "이 사용자는 로그인 할 수 없습니다", "ACCOUNT_LOCKED");
			return;
		}
		
		String userId = request.getParameter("username");
		String ip = request.getRemoteAddr();

		writeResponse(response, loginAttemptService.loginFailed(userId, ip), "LOGIN_FAILED");
	}



}
