package buttle.apptpl.cmm.cd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS5040001Dto extends BaseDto {

    @Schema(description = "코드 그룹", type = "string")
    private String cdGroup;

    @Schema(description = "코드 그룹 명", type = "string")
    private String cdGroupNm;

    @Schema(description = "사용 여부", type = "string")
    private String useYn;

    @Schema(description = "코드 사용처 코드", type = "string")
    private String cdPlcusCd;
} 