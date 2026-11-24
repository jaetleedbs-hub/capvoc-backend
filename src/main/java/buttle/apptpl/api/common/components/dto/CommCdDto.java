package buttle.apptpl.api.common.components.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CommCdDto extends BaseDto {
    private String value;
    private String name;
    private String cdGroup;
    private String sortSn;
}
