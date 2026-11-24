package buttle.apptpl.cmm.notification.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "알림 조회 응답")
public class NotificationsDto {

    // 공지사항 데이터 목록
    List<NoticeDto> systemNotice;

    // 서비스 답변 데이터 목록
    List<ServiceAnswerDto> serviceAnswer;

    // 어드민 UMS 데이터 목록
    List<UmsSedngInfo> adminMessage;
   
    
}
