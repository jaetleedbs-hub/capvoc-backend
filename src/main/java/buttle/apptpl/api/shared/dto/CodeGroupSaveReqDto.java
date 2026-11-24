package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 그룹 저장 요청")
public class CodeGroupSaveReqDto {
    @Schema(description = "회원사유형", required = true)
    private String cmpyTypeCd;
    @Schema(description = "코드그룹", required = true)
    private String cdGroup;
    @Schema(description = "코드그룹명", required = true)
    private String cdGroupNm;
    @Schema(description = "비고내용")
    private String rmrkCn;
    @Schema(description = "사용여부")
    private String useYn;
    @Schema(description = "코드사용처코드")
    private String cdPlcusCd;
}
