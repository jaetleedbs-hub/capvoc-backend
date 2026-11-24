package buttle.apptpl.common.mybatis;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.springframework.data.domain.Page;

/**
 * Mybatis에서 {@link Page}를 사용하기 위해 커스터마이징한 Object Factory.
 *
 * <pre>
 * Mybatis에서는 {@link Page}를 컬렉션으로 인지하지 못하여 selectOne만 실행하므로 {@link #isCollection(Class)}을
 * 오버라이딩하여 리턴 타입이 Page인 경우에도 selectList를 호출하게 함.
 *
 * 또, 리턴 타입이 Page일 때 {@link DefaultObjectFactory}에는 실제 구현체가 정의되어있지 않으므로 {@link #create(Class)} 메소드를
 * 오버라이딩하여 Page의 구현체인 {@link MybatisPageImpl}을 리턴하게 함.
 * </pre>
 */
public class PageCustomObjectFactory extends DefaultObjectFactory {

    @Override
    public <T> boolean isCollection(Class<T> type) {
        return Page.class.isAssignableFrom(type) || super.isCollection(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> type) {
        if (Page.class.isAssignableFrom(type)) {
            MybatisPagingContext context = MybatisPagingContextHandler.getContext();
            if (context == null) {
                // 리턴 타입이 Page인데 context가 null인 경우 pageable 파라미터를 넘기지 않은 경우로 간주
                throw new IllegalStateException("Page를 사용하려면 Pageable 파라미터를 넘겨야 합니다.");
            }
            // Context clear는 MybatisPrepareInterceptor에서 다음 쿼리 실행 전에 수행
            // (동일 요청 내 여러 Page 조회를 지원하기 위해 여기서는 clear하지 않음)
            return (T) new MybatisPageImpl<>(context.getPageable(), context.getCalculatedTotalSize());
        }
        return super.create(type);
    }
} 