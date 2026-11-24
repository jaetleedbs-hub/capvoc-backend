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
 * 사용자별권한조회 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3030001Dto extends BaseDto {

    @Schema(description = "메뉴 ID 또는 명", type = "String")
    private String menuIdOrNm;

    @Schema(description = "사용자 ID", type = "String")
    private String userId;

    @Schema(description = "조직 코드", type = "String")
    private String ognzCd;

    @Schema(description = "프로그램 ID 또는 명", type = "String")
    private String prgrmIdOrNm;
} 