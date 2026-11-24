package buttle.apptpl.common.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MyBatis 조회 결과 자동 복호화 어노테이션
 * VO 필드에 적용하면 MyBatis 조회 후 자동으로 복호화됩니다.
 * 
 * @author AICC Development Team
 * @since 2025-11-10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Decrypt {
}

