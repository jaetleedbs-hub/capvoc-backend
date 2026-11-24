package buttle.apptpl.cmm.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0030003Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;
} 