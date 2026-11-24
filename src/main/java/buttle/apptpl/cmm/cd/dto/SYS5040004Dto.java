package buttle.apptpl.cmm.cd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS5040004Dto extends BaseDto {

    @Schema(description = "코드", type = "string")
    private String cd;

    @Schema(description = "코드 그룹", type = "string")
    private String cdGroup;

    @Schema(description = "로케일", type = "string")
    private String locale;

    @Schema(description = "코드 명", type = "string")
    private String cdNm;

    @Schema(description = "코드 구분", type = "string")
    private String cdSe;

    @Schema(description = "사용 여부", type = "string")
    private String useYn;

    @Schema(description = "정렬 일련번호", type = "integer")
    private Integer sortSn;

    @Schema(description = "등록자 ID", type = "string")
    private String rgtrId;

    @Schema(description = "등록자 명", type = "string")
    private String rgtrIdNm;

    @Schema(description = "등록 일시", type = "string")
    private String regDt;

    @Schema(description = "수정자 ID", type = "string")
    private String mdfrId;

    @Schema(description = "수정자 명", type = "string")
    private String mdfrIdNm;

    @Schema(description = "수정 일시", type = "string")
    private String mdfcnDt;
} 