package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 그룹 검색 요청")
public class CodeGroupSearchReqDto {
    @Schema(description = "회원사유형", required = true)
    private String cmpyTypeCd;
    @Schema(description = "코드그룹")
    private String cdGroup;
    @Schema(description = "코드그룹명")
    private String cdGroupNm;
    @Schema(description = "사용여부")
    private String useYn;
}
