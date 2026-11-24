package buttle.apptpl.common.polling.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PollingMapper {

    /**
     * 미확인 공지 건수.
     *
     * <p>desk 의 "새 것 있나" 폴링과 관리콘솔 알림 배지가 함께 쓴다
     * ({@code UserPollingService} 참조). 같은 계산을 두 벌 두지 않기 위해 합친 것이다.
     *
     * @param memberId 사용자 ID
     * @return 미확인 건수 (없으면 0)
     */
    int selectPollingNotice(@Param("memberId") String memberId);

    /**
     * admin 폴링 서비스 답변 조회 여부 조회
     * 
     * @param cmpyId 회사 ID
     * @param memberId 회원 ID
     * @return admin 폴링 서비스 답변 조회 여부
     */
    Integer selectPollingServiceAnswer(@Param("cmpyId") String cmpyId, @Param("memberId") String memberId);


    /**
     * admin 폴링 어드민 알림 제목/내용 정보 조회
     * 
     * @param memberId 회원 ID
     * @return admin 폴링 어드민 알림 제목/내용 정보
     */
    Integer selectUmsSedngInfo(@Param("memberId") String memberId);

}
