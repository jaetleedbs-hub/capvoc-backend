package buttle.apptpl.log.userchange.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import buttle.apptpl.log.userchange.vo.SYS6030001Vo;
import buttle.apptpl.log.userchange.vo.SYS6030002Vo;

/**
 * SYS6030 사용자변경이력 Mapper
 */
@Mapper
public interface UserChangeHistoryMapper {

    /**
     * 사용자 변경 이력 조회
     * @param param 조회 조건
     * @return 사용자 변경 이력 목록
     */
    Page<SYS6030002Vo> selectUserChgHistList(SYS6030001Vo param, Pageable pageable);
} 