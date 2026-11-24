package buttle.apptpl.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 커스텀 인증 예외 클래스
 * CustomUserDetailsService에서 구체적인 오류 메시지를 LoginFailureHandler로 전달하기 위해 사용
 */
public class CustomAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 1L;
    
    private final String errorCode;
    private final String detailMessage;

    public CustomAuthenticationException(String msg) {
        super(msg);
        this.errorCode = "UNKNOWN";
        this.detailMessage = msg;
    }

    public CustomAuthenticationException(String msg, String errorCode) {
        super(msg);
        this.errorCode = errorCode;
        this.detailMessage = msg;
    }

    public CustomAuthenticationException(String msg, String errorCode, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
        this.detailMessage = msg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
} 