package buttle.apptpl.common.security.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 비밀번호 해시 — <b>BCrypt 하나만</b> 쓴다.
 *
 * <p><b>왜 「하나만」을 명시하는가.</b> 이 자리는 관용이 곧 취약점이 되는 자리다.
 * 「옛 형식도 받아 준다」를 한 줄 넣으면, 그 형식은 <b>영원히 살아남는다</b> —
 * 아무도 그것을 쓰는 계정이 남았는지 세지 않고, 세더라도 원문이 없어 일괄 이행이 불가능하기 때문이다.
 * 그래서 새로 시작하는 프로젝트는 <b>처음부터 하나로</b> 둔다.
 *
 * <p>이미 다른 형식으로 저장된 데이터를 이관해 와야 한다면, 폴백을 여기 넣지 말고
 * <b>이행 계획</b>으로 다룬다 — 로그인 성공 시점에 재해싱하고({@code upgradeEncoding} 을 켜고
 * {@code UserDetailsPasswordService} 를 배선한다), 끝내 로그인하지 않는 계정은 강제 초기화한다.
 * 그 경우에도 <b>이행이 끝나면 폴백을 걷어낸다.</b> 걷어내는 날짜를 정하지 않은 폴백은 영구 폴백이다.
 *
 * <p><b>강도(strength)</b>는 설정으로 뺐다. 기본 10은 2026년 기준 무난하지만,
 * 하드웨어가 빨라지면 올려야 하는 값이라 코드에 박으면 그날 재배포가 필요하다.
 * 올리면 <b>기존 해시는 그대로 검증된다</b>(강도는 해시 문자열 안에 들어 있다) — 새로 만드는 것만 강해진다.
 */
@Component
public class CustomPasswordEncoder extends BCryptPasswordEncoder {

    public CustomPasswordEncoder(@Value("${security.password.bcrypt-strength:10}") int strength) {
        super(strength);
    }

    /**
     * 저장된 해시가 BCrypt 가 아닌지 알린다.
     *
     * <p>{@code DaoAuthenticationProvider} 가 인증 성공 후 호출하고, {@code true} 면
     * {@code UserDetailsPasswordService.updatePassword()} 로 새 해시를 저장한다.
     * BCrypt 만 쓰는 지금은 「이행할 것이 없다」는 뜻으로 늘 {@code false} 다.
     */
    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}
