package buttle.apptpl.common.jwt;

import org.springframework.security.core.Authentication;
import buttle.apptpl.common.framework.common.vo.SecUser;

public interface JwtRepository {
    void saveRefreshToken(String refreshToken, SecUser secMbr);
    Authentication checkRefreshToken(String refreshToken);
    void removeRefreshToken(String refreshToken);
} 