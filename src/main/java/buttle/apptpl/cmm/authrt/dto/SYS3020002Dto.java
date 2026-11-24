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
 * 사용자권한등급목록 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3020002Dto extends BaseDto {

    @Schema(description = "권한 코드", type = "String")
    private String authrtCd;

    @Schema(description = "권한 명", type = "String")
    private String authrtNm;

    @Schema(description = "권한사용자수", type = "Integer")
    private Integer authrtUserCnt;

    @Schema(description = "권한 구분", type = "String")
    private Integer authrtSe;
}