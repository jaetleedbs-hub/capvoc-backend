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
 * 사용자별권한 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3030002Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "String")
    private String userId;

    @Schema(description = "사용자 명", type = "String")
    private String userNm;

    @Schema(description = "조직 코드", type = "String")
    private String ognzCd;

    @Schema(description = "조직 명", type = "String")
    private String ognzNm;

    @Schema(description = "권한 코드", type = "String")
    private String authrtCd;

    @Schema(description = "권한 코드 명", type = "String")
    private String authrtCdNm;
} 