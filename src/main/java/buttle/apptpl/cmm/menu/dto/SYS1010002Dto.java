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
 * 메뉴관리 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS1010002Dto extends BaseDto {

    @Schema(description = "메뉴 ID", type = "String")
    private String menuId;

    @Schema(description = "메뉴 명", type = "String")
    private String menuNm;

    @Schema(description = "URL 주소", type = "String")
    private String urlAddr;

    @Schema(description = "새창 여부", type = "String")
    private String npagYn;

    @Schema(description = "상위 메뉴 ID", type = "String")
    private String upMenuId;

    @Schema(description = "정렬 일련번호", type = "Integer")
    private Integer sortSn;

    @Schema(description = "수정자 ID", type = "String")
    private String mdfrId;

    @Schema(description = "수정자 ID", type = "String")
    private String mdfrIdNm;

    @Schema(description = "수정 일시", type = "String")
    private String mdfcnDt;

    @Schema(description = "행 타입 (C:등록, U:수정, D:삭제, R:읽기)", type = "String")
    private String rowtype;

    @Schema(description = "화면 크기", type = "String")
    private String scrnSize;

    @Schema(description = "사용 여부", type = "String")
    private String menuUseYn;
}