package buttle.apptpl.log.userlogin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import buttle.apptpl.log.userlogin.vo.SYS6010001Vo;
import buttle.apptpl.log.userlogin.vo.SYS6010002Vo;

/**
 * SYS6010 사용자로그인이력 Mapper
 */
@Mapper
public interface UserLoginHistoryMapper {

    /**
     * 일별 사용자 로그인 이력 조회
     * @param param 조회 조건
     * @return 일별 사용자 로그인 이력 목록
     */
    Page<SYS6010002Vo> selectDailyUserLoginHistList(SYS6010001Vo param, Pageable pageable);
} 