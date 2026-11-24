package buttle.apptpl.cmm.authrt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 사용자권한코드관리 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3010001Dto extends BaseDto {

    @Schema(description = "권한 코드", type = "String")
    private String authrtCd;

    @Schema(description = "권한 코드 또는 명", type = "String")
    private String authrtCdOrNm;

    @Schema(description = "조직 조회 범위 코드", type = "String")
    private String ognzInqRangCd;
}