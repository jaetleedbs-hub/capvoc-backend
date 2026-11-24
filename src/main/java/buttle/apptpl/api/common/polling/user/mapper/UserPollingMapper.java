package buttle.apptpl.api.common.polling.user.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 공통 컴포넌트 Mapper
 */
@Mapper
public interface UserPollingMapper {







    /**
     * 예약 메모 조회 건수
     * @param userId
     * @return
     */
    int selectReservationMemosCount(String userId);


} 