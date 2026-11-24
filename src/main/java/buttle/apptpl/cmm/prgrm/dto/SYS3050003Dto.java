package buttle.apptpl.cmm.prgrm.dto;

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
public class SYS3050003Dto extends BaseDto {
    
    @Schema(description = "저장구분YN", type = "string")
    private String saveSeYn;
    
    @Schema(description = "프로그램 ID", type = "string")
    private String prgrmId;
    
    @Schema(description = "프로그램 명", type = "string")
    private String prgrmNm;
    
    @Schema(description = "권한 코드", type = "string")
    private String authrtCd;
    
    @Schema(description = "http 메소드", type = "string")
    private String httpMethod;
    
    @Schema(description = "URL 주소 패턴", type = "string")
    private String urlAddrPtrn;
    
    @Schema(description = "정렬순서", type = "integer")
    private Integer sortSn;
} 