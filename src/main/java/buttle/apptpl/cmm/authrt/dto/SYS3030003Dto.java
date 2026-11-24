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
 * 사용자별예외권한 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3030003Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "String")
    private String userId;

    @Schema(description = "메뉴 ID", type = "String")
    private String menuId;

    @Schema(description = "메뉴 명", type = "String")
    private String menuNm;

    @Schema(description = "권한 부여 구분 코드", type = "String")
    private String authrtGrntSeCd;

    @Schema(description = "권한 부여 구분 코드 명", type = "String")
    private String authrtGrntSeCdNm;
} 