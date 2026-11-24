package buttle.apptpl.cmm.authrt.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.authrt.vo.SYS3030001Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3030002Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3030003Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3030004Vo;
import java.util.List;

/**
 * 사용자별권한관리 Mapper
 */
@Mapper
public interface UserAuthorityMapper {

    /**
     * 사용자 별 권한관리 대상 사용자 목록 조회
     */
    List<SYS3030002Vo> selectUserTypeAuthMgmtUserList(SYS3030001Vo param);

    /**
     * 사용자별 메뉴 권한 목록 조회
     */
    List<SYS3030003Vo> selectUserTypePgmList(SYS3030001Vo param);

    /**
     * 사용자별 예외 메뉴 권한 목록 조회
     */
    List<SYS3030003Vo> selectUserTypeExcpPgmList(SYS3030001Vo param);

    /**
     * 전체 메뉴 목록 조회
     */
    List<SYS3030003Vo> selectAllMenuList(SYS3030001Vo param);

    /**
     * 사용자별 메뉴 권한 설정 조회
     */
    List<SYS3030003Vo> selectUserMenuAuthList(SYS3030001Vo param);

    /**
     * 전체 프로그램 목록 조회
     */
    List<SYS3030004Vo> selectAllProgramList(SYS3030001Vo param);

    /**
     * 사용자별 프로그램 권한 목록 조회
     */
    List<SYS3030004Vo> selectUserProgramAuthList(SYS3030001Vo param);
} 