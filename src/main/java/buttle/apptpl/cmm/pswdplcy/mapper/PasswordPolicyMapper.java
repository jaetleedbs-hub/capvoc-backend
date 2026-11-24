package buttle.apptpl.cmm.pswdplcy.mapper;

import org.apache.ibatis.annotations.Mapper;

import buttle.apptpl.cmm.pswdplcy.vo.SYS5020001Vo;
import buttle.apptpl.cmm.pswdplcy.vo.SYS5020002Vo;

@Mapper
public interface PasswordPolicyMapper {

    /**
     * 비밀번호 정책 조회
     * @param param 조회 조건
     * @return 비밀번호 정책 
     */
    SYS5020002Vo selectPasswordPolicy(SYS5020001Vo param);

} 