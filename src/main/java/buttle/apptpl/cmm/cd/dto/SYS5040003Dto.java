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
public class SYS5040003Dto extends BaseDto {

    @Schema(description = "코드 그룹", type = "string")
    private String cdGroup;

    @Schema(description = "코드", type = "string")
    private String cd;

    @Schema(description = "로케일코드(lcal_cd)", type = "string")
    private String locale;

    @Schema(description = "코드 명", type = "string")
    private String cdNm;

    @Schema(description = "사용 여부", type = "string")
    private String useYn;
} 