package buttle.apptpl.auth.password.exception;

import micoai.corebase.exception.InvalidationException;

/**
 * 비밀번호 오류 누적으로 계정이 잠긴(사용중지) 경우 발생하는 예외.
 *
 * <p>컨트롤러가 예외 <b>메시지 텍스트</b>가 아니라 <b>타입</b>으로 계정잠금 상황
 * (HTTP 473 응답·강제 로그아웃)을 식별하기 위한 전용 예외이다. 메시지 문구가
 * 바뀌거나 다국어 처리가 적용되어도 분기가 깨지지 않는다.</p>
 *
 * <p>{@link InvalidationException} 을 상속하므로, 별도 처리되지 않는 경로에서는
 * 기존 검증 예외와 동일하게 취급된다.</p>
 */
public class AccountLockedException extends InvalidationException {

    private static final long serialVersionUID = 1L;

    public AccountLockedException(String message) {
        super(message);
    }
}
