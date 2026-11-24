package buttle.apptpl.common.mybatis;

import lombok.*;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.springframework.data.domain.Pageable;

/**
 * Mybatis 페이징 처리를 위한 Context
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Getter
@ToString
class MybatisPagingContext {

    /**
     * PreparedStatement 파라미터 핸들러
     */
    private ParameterHandler parameterHandler;

    /**
     * count sql
     */
    private String countSql;

    /**
     * Pageable 객체
     */
    private Pageable pageable;

    /**
     * count query로 계산된 total count
     */
    private long calculatedTotalSize;

    MybatisPagingContext(ParameterHandler parameterHandler, String countSql, Pageable pageable) {
        this.parameterHandler = parameterHandler;
        this.countSql = countSql;
        this.pageable = pageable;
    }
} 