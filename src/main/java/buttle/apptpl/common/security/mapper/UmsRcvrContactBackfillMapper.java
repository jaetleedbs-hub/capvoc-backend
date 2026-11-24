package buttle.apptpl.common.security.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * [PII-6] UMS 수신자 연락처 <b>백필</b> — 평문·레거시 CBC 로 남은 값을 GCM + 검색 키로 옮긴다.
 */
@Mapper
public interface UmsRcvrContactBackfillMapper {

    /**
     * 아직 옮기지 않은 행.
     *
     * <p>⚠ 결과는 <b>조회 복호 인터셉터를 이미 거친다</b> — GCM 이던 값은 평문으로 돌아온다.
     * 레거시 CBC 는 접두가 없어 그대로 온다. 그래서 서비스가 한 번 더 벗긴다.
     */
    List<Map<String, Object>> selectPending(@Param("limit") int limit);

    /** 한 행의 연락처와 검색 키를 채운다. 값은 서비스가 이미 암호화·산출해서 넘긴다. */
    int updateOne(@Param("umsId") String umsId,
                  @Param("umsSn") Integer umsSn,
                  @Param("telno") String telno,
                  @Param("telnoBidx") String telnoBidx,
                  @Param("email") String email,
                  @Param("emailBidx") String emailBidx);

    /** 남은 대상 수 — <b>완료 조건</b>이다(0 이어야 끝난 것이다). */
    int countPending();

    /** 연락처가 하나라도 있는 행 수(분모). */
    int countTotal();

    // ── [PII-6 잔여] 임직원·계약담당자 연락처 ──────────────────────────────
    // 검색 키가 없다(조회 술어 0곳). 그래서 암호화만 하고 bidx 는 만들지 않는다.

    /** 아직 GCM 이 아닌 임직원 행. */
    List<Map<String, Object>> selectUserPending(@Param("limit") int limit);

    /** 임직원 한 행의 연락처를 채운다. 값은 서비스가 이미 암호화해서 넘긴다. */
    int updateUserOne(@Param("userId") String userId,
                      @Param("mblTelNo") String mblTelNo,
                      @Param("homeTelNo") String homeTelNo,
                      @Param("emlAddr") String emlAddr);

    /** 남은 임직원 대상 수. */
    int countUserPending();

    /** 아직 GCM 이 아닌 회원사(계약담당자) 행. */
    List<Map<String, Object>> selectCmpyPending(@Param("limit") int limit);

    /** 회원사 한 행의 담당자 연락처를 채운다. */
    int updateCmpyOne(@Param("cmpyId") String cmpyId,
                      @Param("cntctPhone") String cntctPhone,
                      @Param("cntctEmail") String cntctEmail);

    /** 남은 회원사 대상 수. */
    int countCmpyPending();
}
