package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 그룹 목록 응답")
public class CodeGroupListResDto {
    @Schema(description = "회원사유형")
    private String cmpyTypeCd;
    @Schema(description = "코드그룹")
    private String cdGroup;
    @Schema(description = "코드그룹명")
    private String cdGroupNm;
    @Schema(description = "비고내용")
    private String rmrkCn;
    @Schema(description = "사용여부")
    private String useYn;
    @Schema(description = "코드사용처코드")
    private String cdPlcusCd;
    @Schema(description = "등록일시")
    private String regDt;
}
