package buttle.apptpl.cmm.notification.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsRequestDto {
    private String cmpyId;
    private String mbrId;
    private String unreadOnly;
    private String viewUseYn;
}
