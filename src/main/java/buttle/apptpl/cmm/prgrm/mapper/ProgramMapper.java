package buttle.apptpl.cmm.prgrm.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.prgrm.vo.SYS3040001Vo;

import java.util.List;

/**
 * 프로그램관리 Mapper
 */
@Mapper
public interface ProgramMapper {

    /**
     * API 조회
     * @param param 조회 조건
     * @return 프로그램 목록
     */
    List<SYS3040001Vo> ApAuthList(SYS3040001Vo param);

} 