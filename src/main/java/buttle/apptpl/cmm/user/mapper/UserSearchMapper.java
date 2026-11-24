package buttle.apptpl.cmm.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.user.vo.SYS0040001Vo;
import buttle.apptpl.cmm.user.vo.SYS0040002Vo;
import buttle.apptpl.cmm.user.vo.SYS0040003Vo;

import java.util.List;

/**
 * 사용자찾기 Mapper
 */
@Mapper
public interface UserSearchMapper {

    /**
     * 사용자 소속 조직 조회
     * @param param 검색 조건
     * @return 조직 목록
     */
    List<SYS0040002Vo> selectUserBelongOrgList(SYS0040001Vo param);

    /**
     * 조직별 사용자 정보 조회
     * @param param 검색 조건
     * @return 사용자 목록
     */
    List<SYS0040003Vo> selectOrgTypeUserInfoList(SYS0040001Vo param);
} 