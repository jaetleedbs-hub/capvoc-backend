package buttle.apptpl.cmm.authrt.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.authrt.vo.SYS3010001Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3010002Vo;
import java.util.List;

/**
 * 사용자권한코드관리 Mapper
 */
@Mapper
public interface AuthorityCodeMapper {

    /**
     * 사용자 권한 코드 조회
     */
    List<SYS3010002Vo> selectUserAuthCdList(SYS3010001Vo param);

    /**
     * 사용자 권한 코드 상세 조회
     */
    SYS3010002Vo selectUserAuthCdDtl(SYS3010001Vo param);
} 