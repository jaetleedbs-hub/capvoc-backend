package buttle.apptpl.log.access.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.log.access.vo.SYS6040001Vo;
import buttle.apptpl.log.access.vo.SYS6040002Vo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * SYS6040 프로그램접속이력 Mapper
 */
@Mapper
public interface ProgramAccessHistoryMapper {

    /**
     * 프로그램 접속 이력 조회
     * @param param 조회 조건
     * @return 프로그램 접속 이력 목록
     */
    Page<SYS6040002Vo> selectPgmConnHistList(SYS6040001Vo param, Pageable pageable);

} 