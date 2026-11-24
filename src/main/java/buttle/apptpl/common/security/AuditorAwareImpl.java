package buttle.apptpl.common.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import buttle.apptpl.common.constant.AuditConstants;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;

public class AuditorAwareImpl implements AuditorAware<String> { // 사용자 ID 타입에 맞게 String 또는 Long 등으로 변경
    @Override
    public Optional<String> getCurrentAuditor() {
    	
        SecUser user = SecurityUtils.getCurrentUser();
        if (user == null ) {
            return Optional.of(AuditConstants.SYSTEM); // 인증되지 않은 경우 기본값
        }
        
        return Optional.of(user.getUserId()); // 또는 ((MyUserDetails) authentication.getPrincipal()).getUserId();
    }
}