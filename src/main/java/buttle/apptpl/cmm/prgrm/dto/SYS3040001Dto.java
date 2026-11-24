package buttle.apptpl.cmm.prgrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 프로그램등록 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3040001Dto extends BaseDto {

    @Schema(description = "프로그램 ID", type = "string")
    private String prgrmId;

    @Schema(description = "프로그램 명", type = "string")
    private String prgrmNm;

    @Schema(description = "http 메소드", type = "string")
    private String httpMethod;

    @Schema(description = "URL 주소 패턴", type = "string")
    private String urlAddrPtrn;

    @Schema(description = "정렬 일련번호", type = "integer")
    private Integer sortSn;

    @Schema(description = "프로그램 검색", type = "string")
    private String prgrmIdOrNm;

    @Schema(description = "등록자 ID", type = "string")
    private String rgtrId;

    @Schema(description = "등록일시", type = "string")
    private String regDt;

    @Schema(description = "수정자 ID", type = "string")
    private String mdfrId;

    @Schema(description = "수정일시", type = "string")
    private String mdfcnDt;
} 