package buttle.apptpl.cmm.prgrm.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.prgrm.vo.SYS3050001Vo;
import buttle.apptpl.cmm.prgrm.vo.SYS3050002Vo;
import buttle.apptpl.cmm.prgrm.vo.SYS3050003Vo;

import java.util.List;

/**
 * SYS3050 프로그램권한관리 Mapper
 */
@Mapper
public interface ProgramAuthorityMapper {

        /**
     * 사용자 권한 등급 목록 조회
     */
    List<SYS3050002Vo> selectUserAuthListList(SYS3050001Vo param);

    /**
     * 사용자 권한 등급 별 프로그램 목록 조회
     */
    List<SYS3050003Vo> selectUserAuthTypePrgrmList(SYS3050001Vo param);
} 