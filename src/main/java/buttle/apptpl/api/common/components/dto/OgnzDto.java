package buttle.apptpl.api.common.components.dto;

import lombok.*;
import lombok.experimental.SuperBuilder; import buttle.apptpl.common.data.BaseDto;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class OgnzDto extends BaseDto {
    private String ognzCd;
    private String ognzNm;
    private String ognzLvl;
    private String upOgnzCd;
    private String sortSn;
    private String useYn;
}
