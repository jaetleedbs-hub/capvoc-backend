package buttle.apptpl.cmm.syscrtr.dto;

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
@Schema(description = "기준정보 요청")
public class SYS5010001Dto extends BaseDto {

    @Schema(description = "시스템 기준 코드", type = "String")
    private String sysCrtrCd;

    @Schema(description = "시스템 기준 코드 또는 명", type = "String")
    private String sysCrtrCdOrNm;

    @Schema(description = "시스템 기준 유형 코드", type = "String")
    private String sysCrtrTypeCd;

    @Schema(description = "사용 여부", type = "String")
    private String useYn;

}