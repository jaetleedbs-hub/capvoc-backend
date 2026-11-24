package buttle.apptpl.api.common.polling.user.dto;

import java.util.List;

import lombok.Data;

/**
 * 통합 사용자 폴링 응답 DTO
 */
@Data
public class UserPollingDto {
    
    /**
     * 사용자 요약 정보
     */
    private UserSummaryDto userSummary;
    
    /**
     * 공지사항 목록
     */
    private List<NoticeDto> notices;
    
    /**
     * 세션 상태 (정상: true, 세션 만료/오류: false)
     */
    private boolean sessionValid;
    
    /**
     * 세션 오류 메시지 (세션이 유효하지 않을 때)
     */
    private String sessionMessage;

    private CtiBoardDto ctiBoardDto;
    
    public UserPollingDto() {
        this.sessionValid = true;
    }
    
    public UserPollingDto(UserSummaryDto userSummary, List<NoticeDto> notices) {
        this.userSummary = userSummary;
        this.notices = notices;
        this.sessionValid = true;
    }
    
    /**
     * 세션 오류가 있는 경우 생성자
     */
    public UserPollingDto(String sessionMessage) {
        this.sessionValid = false;
        this.sessionMessage = sessionMessage;
        this.userSummary = new UserSummaryDto();
        this.notices = List.of();
    }

    public UserPollingDto(UserSummaryDto userSummary, List<NoticeDto> notices, CtiBoardDto ctiBoardDto) {
        this.userSummary = userSummary;
        this.notices = notices;
        this.sessionValid = true;
        this.ctiBoardDto = ctiBoardDto;
    }
}
