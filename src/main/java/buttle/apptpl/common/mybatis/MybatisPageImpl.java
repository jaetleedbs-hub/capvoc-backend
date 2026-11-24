package buttle.apptpl.common.mybatis;

import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mybatis 페이징 용으로 임의 구현한 Page 인터페이스 구현체
 *
 * <pre>
 * 기본적으로 {@link PageImpl}의 구현과 거의 동일하지만 {@link #add}와 {@link #addAll(List)} 함수를 추가하여
 * {@link #content}에 추가할 수 있게 구현함.
 * </pre>
 *
 * @param <T> 타입
 */
@EqualsAndHashCode
@SuppressWarnings("unchecked")
class MybatisPageImpl<T> implements Page<T> {

    private final List<T> content = new ArrayList<>();

    private final Pageable pageable;

    private final long total;

    /**
     * {@link PageImpl PageImpl}의 구현을 참조함
     *
     * @param content  컨텐츠
     * @param pageable Pageable 객체
     * @param total    전체 카운트
     * @see PageImpl#PageImpl(List, Pageable, long)
     */
    private MybatisPageImpl(List<T> content, Pageable pageable, long total) {
        this.content.addAll(content);
        this.pageable = pageable;
        this.total = pageable.toOptional()
                .filter(it -> !content.isEmpty())
                .filter(it -> it.getOffset() + it.getPageSize() > total)
                .map(it -> it.getOffset() + content.size())
                .orElse(total);
    }

    MybatisPageImpl(Pageable pageable, long total) {
        this(Collections.emptyList(), pageable, total);
    }

    public int getNumber() {
        return pageable.isPaged() ? pageable.getPageNumber() : 0;
    }

    public int getSize() {
        return pageable.isPaged() ? pageable.getPageSize() : content.size();
    }

    public int getNumberOfElements() {
        return content.size();
    }

    public boolean hasPrevious() {
        return getNumber() > 0;
    }

    public boolean isFirst() {
        return !hasPrevious();
    }

    public boolean isLast() {
        return !hasNext();
    }

    @Override
    public boolean hasNext() {
        return this.getNumber() + 1 < this.getTotalPages();
    }

    @NonNull
    public Pageable nextPageable() {
        return hasNext() ? pageable.next() : Pageable.unpaged();
    }

    @NonNull
    public Pageable previousPageable() {
        return hasPrevious() ? pageable.previousOrFirst() : Pageable.unpaged();
    }

    public boolean hasContent() {
        return !content.isEmpty();
    }

    @NonNull
    public List<T> getContent() {
        return Collections.unmodifiableList(content);
    }

    @Override
    @NonNull
    public Pageable getPageable() {
        return pageable;
    }

    @Override
    @NonNull
    public Sort getSort() {
        return pageable.getSort();
    }

    @NonNull
    public Iterator<T> iterator() {
        return content.iterator();
    }

    @Override
    public int getTotalPages() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
    }

    @Override
    public long getTotalElements() {
        return total;
    }

    @Override
    @NonNull
    public <U> Page<U> map(@NonNull Function<? super T, ? extends U> converter) {
        return new MybatisPageImpl<>(stream().map(converter).collect(Collectors.toList()), getPageable(), total);
    }

    void add(Object element) {
        content.add((T) element);
    }

    public <E> void addAll(List<E> element) {
        content.addAll((Collection<? extends T>) element);
    }
} 