package buttle.apptpl.common.jwt;

import lombok.Getter;

@Getter
public class JwtTokenException extends RuntimeException {

    private final Integer errorCode;
    private final String detailMessage;

    public JwtTokenException(Integer errorCode, String detailMessage) {
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    public JwtTokenException(String detailMessage) {
        this.errorCode = 400;
        this.detailMessage = detailMessage;
    }

}
