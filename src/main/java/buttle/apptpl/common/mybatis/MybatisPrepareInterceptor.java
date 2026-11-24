package buttle.apptpl.common.mybatis;

import java.sql.Connection;
import java.util.Map;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis Mapper 인터페이스에서 메소드 호출 시 수행 전 전처리 인터셉터
 *
 * <pre>
 * sqlId 를 상단주석으로 추가
 * Pageable이 파라미터로 넘어오면 자동으로 페이징 SQL을 만들어 전달
 * Pageable이 파라미터로 넘어오면 count sql 을 만들어서 context에 저장
 * </pre>
 */
@Slf4j
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class MybatisPrepareInterceptor implements Interceptor {

    private static final String DELEGATE_BOUND_SQL_KEY;
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY;
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY;
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY;

    static {
        DELEGATE_BOUND_SQL_KEY = "delegate.boundSql.sql";
        DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
        DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
        DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MybatisPagingContextHandler.clearContext();
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,
                DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);

        MappedStatement ms = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        SqlCommandType commandType = ms.getSqlCommandType();

        //sqlId annotate
        String sqlId = ms.getId();
        String originalSql = statementHandler.getBoundSql().getSql();
        String sqlIdAnnotation = "/* " + sqlId + " */";
        String sqlIdAnnotationCount = "/* " + sqlId + ".count */";
        String annotatedSql = sqlIdAnnotation + "\n" + originalSql;
        metaStatementHandler.setValue(DELEGATE_BOUND_SQL_KEY, annotatedSql);

        //handling pageable
        if (SqlCommandType.SELECT != commandType) {
            return invocation.proceed();
        }

        Object delegate = metaStatementHandler.getValue("delegate");

        if (delegate instanceof StatementHandler handler) {
            Pageable pageable = getPageableParameter(handler);
            if (pageable != null) {
                log.trace("is pageable paged?: {}", pageable.isPaged());
                if (pageable.isPaged()) {
                    String pagingSql = getPagingSql(annotatedSql, pageable);
                    metaStatementHandler.setValue(DELEGATE_BOUND_SQL_KEY, pagingSql);
                    String countSql = getCountSql(annotatedSql, sqlIdAnnotationCount);
                    MybatisPagingContextHandler.setContext(new MybatisPagingContext(statementHandler.getParameterHandler(), countSql, pageable));
                }
            }
        }

        return invocation.proceed();
    }

    private static Pageable getPageableParameter(StatementHandler handler) {
    	Object parameterObject = handler.getParameterHandler().getParameterObject();
        // Mapper 메소드의 파라미터가 여러 개일 경우 Map의 형태로 전달된다.
        Pageable pageable = null;
        if (parameterObject instanceof Map<?, ?> paramMap) {
            for (Object value : paramMap.values()) {
                // Pageable 타입의 파라미터를 발라서 페이징 처리
                if (value instanceof Pageable p) {
                    pageable = p;
                    break;
                }
            }
        } else if (parameterObject instanceof Pageable p) {
            // Mapper 메소드의 파라미터가 하나일 경우에는 Pageable 객체 자체로 전달됨.
            pageable = p;
        }
        if (pageable == null) {
            log.trace("No pageable parameter found");
        }

        return pageable;
    }
    
    private static String getCountSql(String original, String sqlIdAnnotation) {
        return sqlIdAnnotation + "\n" + "select count(*) from (" + original + ") __count__table__04";
    }

    private static String getPagingSql(String originalSql, Pageable pageable) {
        // MySQL/PostgreSQL LIMIT OFFSET 구문 사용
        return originalSql + "\n" + "LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
    }
} 