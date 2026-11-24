package buttle.apptpl.cmm.prgrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 프로그램권한 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3050001Dto extends BaseDto {

    @Schema(description = "프로그램 ID 또는 명", type = "String")
    private String prgrmIdOrNm;

    @Schema(description = "권한 코드", type = "String")
    private String authrtCd;
} 