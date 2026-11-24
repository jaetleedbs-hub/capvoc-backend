package buttle.apptpl.cmm.ognz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 조직조회 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0020001Dto extends BaseDto {

    @Schema(description = "조직 코드", type = "String")
    private String ognzCd;

    @Schema(description = "조직 코드 또는 명", type = "String")
    private String ognzCdOrNm;

    @Schema(description = "사용 여부", type = "String")
    private String useYn;

    @Schema(description = "권한 여부", type = "String")
    private String authrt;
}