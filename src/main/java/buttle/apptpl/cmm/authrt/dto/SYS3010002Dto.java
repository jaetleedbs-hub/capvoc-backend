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
 * 사용자권한코드관리 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3010002Dto extends BaseDto {

    @Schema(description = "권한 코드", type = "String")
    private String authrtCd;

    @Schema(description = "권한 명", type = "String")
    private String authrtNm;

    @Schema(description = "권한 구분", type = "String")
    private String authrtSe;

    @Schema(description = "조직 조회 범위 코드", type = "String")
    private String ognzInqRangCd;

    @Schema(description = "조직 조회 범위 코드 명", type = "String")
    private String ognzInqRangCdNm;

    @Schema(description = "기본 화면 메뉴 ID", type = "String")
    private String bscScrnMenuId;

    @Schema(description = "기본 화면 메뉴 명", type = "String")
    private String bscScrnMenuIdNm;

    @Schema(description = "정렬일련번호", type = "Integer")
    private Integer sortSn;

    @Schema(description = "사용 여부", type = "String")
    private String useYn;

    @Schema(description = "수정 일시", type = "String")
    private String mdfcnDt;
}