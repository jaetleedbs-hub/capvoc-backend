package buttle.apptpl.cmm.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.user.vo.SYS0030001Vo;
import buttle.apptpl.cmm.user.vo.SYS0030002Vo;
import buttle.apptpl.cmm.user.vo.SYS0030003Vo;
import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 사용자 정보 조회
     * @param param 조회 조건
     * @return 사용자 정보 목록
     */
    List<SYS0030001Vo> selectUserInfoList(SYS0030001Vo param);

    /**
     * 사용자 상세 정보 조회
     * @param param 조회 조건
     * @return 사용자 상세 정보
     */
    SYS0030002Vo selectUserDtlInfo(SYS0030002Vo param);


} 