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
@Schema(description = "기준정보 응답")
public class SYS5010002Dto extends BaseDto {

    @Schema(description = "시스템 기준 코드", type = "String")
    private String sysCrtrCd;

    @Schema(description = "시스템 기준 코드명", type = "String")
    private String sysCrtrCdNm;

    @Schema(description = "시스템 기준 코드 값", type = "String")
    private String sysCrtrCdVl;

    @Schema(description = "시스템 기준 유형 코드", type = "String")
    private String sysCrtrTypeCd;

    @Schema(description = "비고 내용", type = "String")
    private String rmrkCn;

    @Schema(description = "사용 여부", type = "String")
    private String useYn;

}