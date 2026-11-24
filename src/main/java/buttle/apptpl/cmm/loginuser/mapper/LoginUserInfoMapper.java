package buttle.apptpl.cmm.loginuser.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.loginuser.vo.COM1100001Vo;
import buttle.apptpl.cmm.loginuser.vo.COM1100002Vo;
import buttle.apptpl.cmm.loginuser.vo.COM1100003Vo;

/**
 * 로그인사용자정보 Mapper
 */
@Mapper
public interface LoginUserInfoMapper {

    /**
     * 사용자 프로필 조회
     * @param param 조회 조건
     * @return 사용자 프로필 정보
     */
    COM1100002Vo selectUserProfile(COM1100001Vo param);

    /**
     * 사용자 이미지 변경
     * @param param 이미지 정보
     * @return 처리 건수
     */
    int updateUserImage(COM1100003Vo param);
} 