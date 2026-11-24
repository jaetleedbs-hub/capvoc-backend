package buttle.apptpl.api.common.components.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import buttle.apptpl.api.common.components.dto.CommCdDto;
import buttle.apptpl.api.common.components.dto.CurrentDatetimeDto;
import buttle.apptpl.api.common.components.dto.OgnzDto;
import buttle.apptpl.api.common.components.dto.UserDto;
import buttle.apptpl.api.shared.vo.BO402SAuthorityListResVo;
import buttle.apptpl.api.shared.vo.BO402SCompanyListResVo;
import buttle.apptpl.cmm.menu.vo.SYS1020002Vo;

/**
 * 공통 컴포넌트 Mapper
 */
@Mapper
public interface CommonComponentMapper {

    /**
     * 사용자 조회
     * @param keyword 사용자 ID 또는 이름
     * @return 사용자 목록
     */
    List<UserDto> selectUserList(@Param("keyword") String keyword, @Param("ognzCd") String ognzCd, @Param("userId") String userId);

    /**
     * 부서 조회
     * @param ognzCode 부서코드
     * @return 부서 정보
     */
    List<OgnzDto> selectOrganization(@Param("keyword") String keyword, @Param("ognzCd") String ognzCd);

    /**
     * 공통코드 조회.
     *
     * ★ locale 을 <반드시> 받는다. 코드표는 언어별로 행이 있어, 거르지 않으면 두 언어의 행이
     *   함께 돌아오고 화면은 그중 아무거나 보여 준다. 값은 {@code LocaleSupport.current()} 로 정한다.
     *
     * @param codeGroup 코드그룹(null 이면 전체)
     * @param locale    언어 코드(ko·en …)
     */
    List<CommCdDto> selectCodeList(@Param("codeGroup") String codeGroup, @Param("locale") String locale);

    /** 공통코드 참조코드 조회. locale 규칙은 {@link #selectCodeList} 와 같다. */
    List<Map<String, String>> selectCodeRefList(@Param("codeGroup") String codeGroup, @Param("locale") String locale);

    /**
     * 회원사별 코드목록 조회
     * @param cmpyTypeCd 회원사 유형
     * @param cdGroup 코드그룹
     * @return 회원사별 코드목록
     */
    List<CommCdDto> selectCompanyCodeList(@Param("cmpyTypeCd") String cmpyTypeCd, @Param("cdGroup") String cdGroup);

    /**
     * 권한 코드에 따른 프로그램 목록 조회
     * @param authrtCd 권한 코드
     * @return 프로그램 목록
     */
    List<String> selectUserAuthrtPrgrmList(String authrtCd);

    /**
     * 사용자 권한 확장 프로그램 목록 조회
     * @param userId 사용자 코드
     * @return 프로그램 목록
     */
    List<String> selectUserExpansionPrgrmList(String userId);

    /**
     * 사용자 권한 예외 목록 조회
     * @param userId 사용자 코드
     * @return 프로그램 목록
     */
    List<String> selectUserExceptPrgrmList(String userId);


    /**
     * 현재 날짜 시간 조회
     * @return 현재 날짜 시간
     */
    CurrentDatetimeDto selectCurrentDatetime();



    /**
     * 공통 프로그램 목록 조회
     * @return 프로그램 목록
     */
    List<String> selectCommonPrgrmList();

    /**
     * 권한 코드에 따른 메뉴 목록 조회
     * @param authrtCd 권한 코드
     * @return 메뉴 목록
     */
    List<SYS1020002Vo> selectUserAuthrtMenuList(String authrtCd);

    /**
     * 사용자 권한 확장 목록 조회
     * @param userId 사용자 ID
     */
    List<SYS1020002Vo> selectUserExpansionMenuList(String userId);

    /**
     * 사용자 권한 예외 목록 조회
     * @param userId 사용자 ID
     * @return 메뉴 목록
     */
    List<SYS1020002Vo> selectUserExceptMenuList(String userId);

    /**
     * 사용자 최근 접속 시간 조회
     * @param userId
     * @return
     */
    long selectLastUserConnectTime(String userId);

    /**
     * 사용자 최근 접속 시간 업데이트
     * @param userId
     */
    void updateUserLastConnectTime(String userId);

    /**
     * 사용자 권한 코드에 따른 조직 조회 가능 여부 조회
     * 사용자가 조회할 수 있는 조직코드인지 여부 validation
     */
    List<String> selectUserAuthrtOgnz(String ognzCd);

    /**
     * 사용자 권한 코드에 따른 사용자 조회 가능 여부 조회
     * 사용자가 조회할 수 있는 사용자코드인지 여부 validation
     */
    List<String> selectUserAuthrtUserId(String ognzCd);

    /**
     * 회원사 목록 조회 (Selectbox용)
     * @return 회원사 목록 (value, name)
     */
    List<BO402SCompanyListResVo> selectCompanyList();

    /**
     * 권한 목록 조회 (Selectbox용)
     * @return 권한 목록 (value, name)
     */
    List<BO402SAuthorityListResVo> selectAuthorityList();
} 