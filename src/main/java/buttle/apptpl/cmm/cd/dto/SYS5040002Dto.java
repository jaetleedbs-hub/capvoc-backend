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
public class SYS5040002Dto extends BaseDto {

    @Schema(description = "코드 그룹", type = "string")
    private String cdGroup;

    @Schema(description = "코드 그룹 명", type = "string")
    private String cdGroupNm;

    @Schema(description = "비고 내용", type = "string")
    private String rmrkCn;

    @Schema(description = "사용 여부", type = "string")
    private String useYn;

    @Schema(description = "코드 사용처 코드", type = "string")
    private String cdPlcusCd;

    @Schema(description = "코드 사용처 코드 명", type = "string")
    private String cdPlcusCdNm;

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