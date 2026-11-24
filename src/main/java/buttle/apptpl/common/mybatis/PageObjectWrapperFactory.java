package buttle.apptpl.common.mybatis;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.data.domain.Page;

/**
 * 리턴 타입이 {@link Page}일 때 {@link MybatisPageObjectWrapper}를 내보내주는 팩토리
 */
public class PageObjectWrapperFactory implements ObjectWrapperFactory {
    @Override
    public boolean hasWrapperFor(Object object) {
        return Page.class.isAssignableFrom(object.getClass());
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        return new MybatisPageObjectWrapper((MybatisPageImpl<?>) object);
    }
} 