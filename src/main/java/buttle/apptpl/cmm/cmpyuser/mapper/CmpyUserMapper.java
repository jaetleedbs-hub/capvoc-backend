package buttle.apptpl.cmm.cmpyuser.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import buttle.apptpl.api.shared.vo.UserSearchReqVo;
import buttle.apptpl.api.shared.vo.UserListResVo;
import buttle.apptpl.api.shared.vo.UserDetailResVo;
import buttle.apptpl.api.shared.vo.UserSaveReqVo;
import buttle.apptpl.api.shared.vo.AuthrtResVo;

import java.util.List;

/**
 * BO401_S 사용자 관리 Mapper
 */
@Mapper
public interface CmpyUserMapper {
    
    /**
     * 사용자 목록 조회
     * @param param 검색 조건
     * @return 사용자 목록
     */
    Page<UserListResVo> getUserList(UserSearchReqVo param, Pageable pageable);
    
    /**
     * 사용자 상세 조회
     * @param userId 사용자ID
     * @return 사용자 상세 정보
     */
    UserDetailResVo getUserDetail(String userId);
    
    /**
     * 사용자 정보 저장 (INSERT)
     * @param param 사용자 정보
     * @return 저장 건수
     */
    int insertUser(UserSaveReqVo param);
    
    /**
     * 사용자 정보 저장 (UPDATE)
     * @param param 사용자 정보
     * @return 수정 건수
     */
    int updateUser(UserSaveReqVo param);
    
    /**
     * 사용자ID 중복 확인
     * @param userId 사용자ID
     * @return 중복 건수 (0: 사용 가능, 1 이상: 중복)
     */
    int checkUserIdDuplicate(String userId);
    
    /**
     * 로그인 정보 초기화
     * @param userId 사용자ID
     * @return 초기화 건수
     */
    int resetUserLogin(String userId);
    
    /**
     * 비밀번호 초기화
     * @param userId 사용자ID
     * @param encryptedPassword 암호화된 임시 비밀번호
     * @return 초기화 건수
     */
    int resetUserPassword(@Param("userId") String userId, @Param("encryptedPassword") String encryptedPassword);
    
    /**
     * 권한(사용자등급) 목록 조회
     * @return 권한 목록
     */
    List<AuthrtResVo> getAuthrtList();
}
