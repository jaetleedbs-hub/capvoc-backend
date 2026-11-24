package buttle.apptpl.api.common.components.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import buttle.apptpl.api.common.components.dto.AuthrtProgramDto;
import buttle.apptpl.api.common.components.dto.CommCdDto;
import buttle.apptpl.api.common.components.dto.CurrentDatetimeDto;
import buttle.apptpl.api.common.components.dto.OgnzDto;
import buttle.apptpl.api.common.components.dto.UserDto;
import buttle.apptpl.common.model.cmpt.entity.CmptMenuEntity;

/**
 * 공통 컴포넌트 Mapper
 */
@Mapper
public interface CommonUserAuthMapper {


    /**
     * 현재 날짜 시간 조회
     * @return 현재 날짜 시간
     */
    CurrentDatetimeDto selectCurrentDatetime();


    /**
     * 권한 코드에 따른 메뉴 목록 조회
     * @param authrtCd 권한 코드
     * @return 메뉴 목록
     */
    List<CmptMenuEntity> selectUserAuthrtMenuList(String authrtCd);

    /**
     * 사용자 권한 확장 목록 조회
     * @param userId 사용자 ID
     */
    List<CmptMenuEntity> selectUserExpansionMenuList(String userId);

    /**
     * 사용자 권한 예외 목록 조회
     * @param userId 사용자 ID
     * @return 메뉴 목록
     */
    List<CmptMenuEntity> selectUserExceptMenuList(String userId);

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
     * 사용자 로그아웃 시간 업데이트
     * @param userId
     */
    void updateUserLogoutTime(String userId);


    /**
     * 사용자 로그아웃 목록 조회
     * @return
     */
    List<String> selectUserLogoutList();


    /**
     * 근태현황 등록 (당일 최초 1행, 이미 있으면 아무 일도 하지 않는다)
     *
     * <p>[V85] 짝이던 {@code selectFirstWorkList}(등록 여부 조회)를 함께 지웠다.
     * 「세고 나서 없으면 넣는다」는 절차 자체가 결함이었고, 그 절차가 사라지면
     * 그 조회는 <쓰는 곳이 없으면서 안전해 보이는> 코드로만 남는다.
     *
     * @param userId 대상 사용자
     */
    void insertWorkList(String userId);



    /**
     * 사용자 최종 로그인 기준 경과여부 확인
     * @param userId
     * @return
     */
    String selectUserHasLapsedLastLogin(String userId);



} 