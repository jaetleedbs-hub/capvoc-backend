package buttle.apptpl.common.mybatis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis 에서 페이징 쿼리를 실행할 때 필요한 정보를 스레드 단위로 가지고 있기 위한 Context Handler
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
final class MybatisPagingContextHandler {

    private static final ThreadLocal<MybatisPagingContext> CONTEXT_THREAD_LOCAL;

    static {
        CONTEXT_THREAD_LOCAL = new ThreadLocal<>();
    }

    static MybatisPagingContext getContext() {
        return CONTEXT_THREAD_LOCAL.get();
    }

    static void setContext(MybatisPagingContext context) {
        CONTEXT_THREAD_LOCAL.set(context);
    }

    static void clearContext() {
        log.trace("Clear MybatisPagingContext");
        CONTEXT_THREAD_LOCAL.remove();
    }
} 