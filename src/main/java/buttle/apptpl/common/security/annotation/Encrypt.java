package buttle.apptpl.common.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MyBatis 쿼리 파라미터 자동 암호화 어노테이션
 * VO 필드에 적용하면 MyBatis 쿼리 실행 전 자동으로 암호화됩니다.
 * 
 * @author AICC Development Team
 * @since 2025-11-10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {
}

