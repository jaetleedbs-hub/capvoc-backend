package buttle.apptpl.auth.password.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import buttle.apptpl.auth.password.vo.COM0020001Vo;

/**
 * COM0020 비밀번호변경 Mapper
 */
@Mapper
public interface PasswordChangeMapper {

    /**
     * 비밀번호 검증
     * @param param 조회 조건
     * @return 비밀번호 검증 결과
     */
    COM0020001Vo validatePassword(COM0020001Vo param);

    /**
     * 비밀번호 재사용 검증용 — 최근 이력의 <b>해시 목록</b>을 가져온다 (최신순).
     *
     * <p>⚠ <b>판정은 여기(SQL)서 하지 않는다.</b> 종전에는
     * {@code countChangePassword(userId, 해시)} 가 {@code WHERE pswd = #{pswd}} 로
     * 해시를 동등비교했다. 저장 형식은 BCrypt 이고 BCrypt 는 호출마다 무작위 솔트를 쓰므로
     * 같은 비밀번호를 다시 넣어도 해시 문자열이 달라 그 조건은 <b>언제나 거짓</b>이었다.
     * BCrypt 는 원문 없이 비교할 수 없어 이 판정은 SQL 로 되돌릴 수 없다 —
     * 원문을 가진 {@code PasswordChangeService} 가 {@code matches()} 로 한다.
     *
     * @param userId 사용자 ID
     * @param days   정책 주기(일). 이 기간 안의 이력만 본다
     * @param limit  검사 상한. BCrypt 비교는 행마다 한 번씩 도는 비싼 연산이라 최근 것부터 이만큼만 본다
     * @return 이력 해시 목록 (최신순). 없으면 빈 목록
     */
    List<String> selectRecentPasswordHashes(@Param("userId") String userId,
                                            @Param("days") int days,
                                            @Param("limit") int limit);
}