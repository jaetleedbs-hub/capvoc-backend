package buttle.apptpl.api.common.polling.user.dto;

import lombok.Data;

@Data
public class UserSummaryDto {
    /*
     * [2026-08-20] 종전에는 필드가 일곱이었다. 업무 이관 3 · 콜백 배정 · 통화 예약 다섯은
     * 이전 시스템(CNS*)의 상담 기능이고 ChatCall 에 없다. 그것을 띄우던 알림 항목은
     * §10.106 에서 제거됐고 화면에 소비자가 0 이라 응답에서도 걷어냈다.
     */






    /**
     * 공지 사항 API 조회 건수
     */
    private int noticesCount;

    /**
     * 예약 메모 조회 건수
     */
    private int reservationMemosCount;
}
