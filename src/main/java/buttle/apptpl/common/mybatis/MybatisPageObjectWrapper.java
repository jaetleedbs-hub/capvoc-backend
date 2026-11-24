package buttle.apptpl.common.mybatis;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * {@link Page}를 감싸는 Mybatis ObjectWrapper
 */
class MybatisPageObjectWrapper implements ObjectWrapper {

    private final MybatisPageImpl<?> page;

    public MybatisPageObjectWrapper(MybatisPageImpl<?> object) {
        this.page = object;
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        // resultType, resultMap 둘다 사용 안한 경우 이 쪽으로 타게 됨.
        throw new UnsupportedOperationException("Mapper XML 선언문에 resultMap 또는 resultType 선언이 필요합니다.");
    }

    @Override
    public String[] getGetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getSetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getGetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasGetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public void add(Object element) {
        page.add(element);
    }

    @Override
    public <E> void addAll(List<E> element) {
        page.addAll(element);
    }
} 