package buttle.apptpl.common.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Pageable;
import buttle.apptpl.common.security.annotation.Decrypt;
import buttle.apptpl.common.util.EncryptionTool;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Collection;

/**
 * Mybatis 쿼리 실행 후 결과 사이즈가 pageable의 page size보다 큰지 작은지 검토하고 count 쿼리 실행하는 interceptor
 */
@Slf4j
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
})
public class MybatisResultInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object proceed = invocation.proceed();
        Connection connection = ((Statement) invocation.getArgs()[0]).getConnection();
        
        if (proceed instanceof Collection<?> collection) {
            // 페이징 처리
            calculateSizeAndProcessCountQuery(collection, connection);
            // 복호화 처리
            decryptCollection(collection);
        } else if (proceed != null) {
            // 단일 객체도 복호화 처리
            decryptFields(proceed);
        }

        return proceed;
    }
    
    /**
     * Collection의 모든 항목에 대해 복호화 처리
     */
    private void decryptCollection(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return;
        }
        for (Object item : collection) {
            decryptFields(item);
        }
    }
    
    /**
     * 객체의 @Decrypt 어노테이션이 있는 필드를 복호화합니다.
     */
    private void decryptFields(Object obj) {
        if (obj == null) {
            return;
        }
        
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            Decrypt decrypt = AnnotationUtils.findAnnotation(field, Decrypt.class);
            if (decrypt == null) {
                continue;
            }
            
            if (!String.class.equals(field.getType())) {
                log.warn("@Decrypt는 String 타입 필드에만 적용 가능합니다. 필드: {}.{}", 
                        clazz.getSimpleName(), field.getName());
                continue;
            }
            
            try {
                field.setAccessible(true);
                String encryptedValue = (String) field.get(obj);
                
                if (encryptedValue != null && !encryptedValue.isEmpty()) {
                    try {
                        String decryptedValue = EncryptionTool.decryptField(encryptedValue);
                        field.set(obj, decryptedValue);
                    } catch (IllegalArgumentException e) {
                        log.warn("복호화할 값이 유효하지 않음: {}.{}, 원본 값 유지", 
                                clazz.getSimpleName(), field.getName());
                    } catch (SecurityException e) {
                        log.error("보안 정책으로 인한 복호화 실패: {}.{}", 
                                clazz.getSimpleName(), field.getName(), e);
                    } catch (RuntimeException e) {
                        log.warn("복호화 처리 실패: {}.{}, 원본 값 유지. 오류: {}", 
                                clazz.getSimpleName(), field.getName(), e.getMessage());
                    } catch (Exception e) {
                        log.warn("예상치 못한 복호화 오류: {}.{}, 원본 값 유지", 
                                clazz.getSimpleName(), field.getName(), e);
                    }
                }
            } catch (IllegalAccessException e) {
                log.warn("필드 접근 실패: {}.{}", clazz.getSimpleName(), field.getName(), e);
            } catch (Exception e) {
                log.warn("필드 복호화 처리 중 오류: {}.{}, 원본 값 유지. 오류: {}", 
                        clazz.getSimpleName(), field.getName(), e.getMessage());
            }
        }
    }

    private void calculateSizeAndProcessCountQuery(Collection<?> collection, Connection connection) throws SQLException {
        MybatisPagingContext context = MybatisPagingContextHandler.getContext();
        if (context != null) {
            long totalCount;

            // total count 계산 최적화
            // @see org.springframework.data.support.PageableExecutionUtils
            Pageable pageable = context.getPageable();

            try (PreparedStatement totalCountStatement = connection.prepareStatement(context.getCountSql())) {
                context.getParameterHandler().setParameters(totalCountStatement);
                if (!pageable.isUnpaged() && pageable.getOffset() != 0L) {
                    if (!collection.isEmpty() && pageable.getPageSize() > collection.size()) {
                        totalCount = pageable.getOffset() + (long) collection.size();
                    } else {
                        totalCount = executeCountQuery(totalCountStatement);
                    }
                } else {
                    if (!pageable.isUnpaged() && pageable.getPageSize() <= collection.size()) {
                        totalCount = executeCountQuery(totalCountStatement);
                    } else {
                        totalCount = collection.size();
                    }
                }
            }

            var newContext = new MybatisPagingContext(null, null, pageable, totalCount);
            MybatisPagingContextHandler.setContext(newContext);
        }
    }

    private long executeCountQuery(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                log.trace("countQuery resultSet: {}", resultSet);
                return resultSet.getLong(1);
            }
        }
        return 0;
    }
} 