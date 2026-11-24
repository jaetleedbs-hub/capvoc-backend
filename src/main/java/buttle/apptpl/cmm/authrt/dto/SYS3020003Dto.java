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
 * 사용자권한메뉴목록 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3020003Dto extends BaseDto {

    @Schema(description = "저장구분YN", type = "String")
    private String saveSeYn;

    @Schema(description = "메뉴 ID", type = "String")
    private String menuId;

    @Schema(description = "권한 코드", type = "String")
    private String authrtCd;

    @Schema(description = "메뉴 명", type = "String")
    private String menuNm;

    @Schema(description = "URL 주소", type = "String")
    private String urlAddr;

    @Schema(description = "새창 여부", type = "String")
    private String npagYn;

    @Schema(description = "상위 메뉴 ID", type = "String")
    private String upMenuId;

    @Schema(description = "상위 메뉴 명", type = "String")
    private String upMenuIdNm;

    @Schema(description = "정렬 일련번호", type = "Integer")
    private Integer sortSn;
}