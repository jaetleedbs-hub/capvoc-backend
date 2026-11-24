package buttle.apptpl.cmm.userip.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.userip.vo.SYS0080001Vo;
import buttle.apptpl.cmm.userip.vo.SYS0080002Vo;

import java.util.List;

/**
 * 사용자IP관리 Mapper
 */
@Mapper
public interface UserIpMapper {

    /**
     * 사용자 IP별 내선정보 조회
     * @param vo 사용자IP관리 요청 VO
     * @return 사용자IP관리 결과 VO 리스트
     */
    List<SYS0080002Vo> selectUserIpTypeExtInfoList(SYS0080001Vo vo);

    /**
     * 사용자 IP별 내선정보 삭제
     * @param lgnIp 삭제할 로그인 IP
     * @return 삭제된 행 수
     */
    int deleteUserIpTypeExtInfo(String lgnIp);
} 