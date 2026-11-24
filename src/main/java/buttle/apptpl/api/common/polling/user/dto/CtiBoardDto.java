package buttle.apptpl.api.common.polling.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CtiBoardDto {
    private final String waitCnt;
    private final String waitMaxTime;
}
