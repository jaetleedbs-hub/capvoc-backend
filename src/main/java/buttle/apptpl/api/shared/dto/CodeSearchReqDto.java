package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 검색 요청")
public class CodeSearchReqDto {
    @Schema(description = "회원사유형", required = true)
    private String cmpyTypeCd;
    @Schema(description = "코드그룹", required = true)
    private String cdGroup;
    @Schema(description = "코드")
    private String cd;
    @Schema(description = "코드명")
    private String cdNm;
    @Schema(description = "로케일")
    private String locale;
    @Schema(description = "사용여부")
    private String useYn;
}
