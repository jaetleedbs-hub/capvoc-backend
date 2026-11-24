package buttle.apptpl.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * SecurityMasking AOP 설정
 * 
 * @author 개발팀
 * @since 2024-01-01
 */
@Configuration
@EnableAspectJAutoProxy
public class SecurityMaskingConfig {
    
    // AOP 자동 프록시 활성화
    // SecurityMaskingAspect가 @Component로 등록되어 있어 자동으로 스캔됨
} 