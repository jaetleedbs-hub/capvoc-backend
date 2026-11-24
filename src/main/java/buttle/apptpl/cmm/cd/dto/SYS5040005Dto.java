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
public class SYS5040005Dto extends BaseDto {

    @Schema(description = "코드", type = "string")
    private String cd;

    @Schema(description = "코드 그룹", type = "string")
    private String cdGroup;

    @Schema(description = "로케일", type = "string")
    private String locale;

    @Schema(description = "참조 코드", type = "string")
    private String rfrncCd;

    @Schema(description = "참조 명", type = "string")
    private String rfrncNm;

    @Schema(description = "참조 값", type = "string")
    private String rfrncVl;
} 