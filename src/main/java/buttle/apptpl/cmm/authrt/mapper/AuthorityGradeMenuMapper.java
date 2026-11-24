package buttle.apptpl.cmm.authrt.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.authrt.vo.SYS3020001Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3020002Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3020003Vo;
import java.util.List;

/**
 * 사용자등급별권한관리 Mapper
 */
@Mapper
public interface AuthorityGradeMenuMapper {

    /**
     * 사용자 권한 등급 목록 조회
     */
    List<SYS3020002Vo> selectUserAuthListList(SYS3020001Vo param);

    /**
     * 사용자 권한 등급 별 메뉴목록 조회
     */
    List<SYS3020003Vo> selectUserAuthTypeMenuList(SYS3020001Vo param);
} 