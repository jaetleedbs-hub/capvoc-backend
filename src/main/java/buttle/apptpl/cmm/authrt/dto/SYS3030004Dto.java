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
 * SYS3030 프로그램 조회 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3030004Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "프로그램 ID", type = "string")
    private String prgrmId;

    @Schema(description = "프로그램 명", type = "string")
    private String prgrmNm;

    @Schema(description = "http 메소드", type = "string")
    private String httpMethod;

    @Schema(description = "URL 주소 패턴", type = "string")
    private String urlAddrPtrn;
    
    @Schema(description = "권한 부여 구분 코드", type = "String")
    private String authrtGrntSeCd;

    @Schema(description = "권한 부여 구분 코드 명", type = "String")
    private String authrtGrntSeCdNm;
} 