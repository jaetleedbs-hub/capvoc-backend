package buttle.apptpl.api.common.polling.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 공지사항 정보
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NoticeDto {
    private String id;

    private String message;
    
    private String noticeId;

    private String boardId;
    
    private String type;

    private String url;
}
