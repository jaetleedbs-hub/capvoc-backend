package buttle.apptpl.cmm.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 메뉴찾기 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS1020002Dto extends BaseDto {

    @Schema(description = "메뉴 ID", type = "string")
    private String menuId;

    @Schema(description = "메뉴 명", type = "string")
    private String menuNm;

    @Schema(description = "URL 주소", type = "string")
    private String urlAddr;

    @Schema(description = "새창 여부", type = "string")
    private String npagYn;

    @Schema(description = "상위 메뉴 ID", type = "string")
    private String upMenuId;

    @Schema(description = "상위 메뉴 명", type = "string")
    private String upMenuIdNm;

    @Schema(description = "정렬 일련번호", type = "integer")
    private Integer sortSn;
}