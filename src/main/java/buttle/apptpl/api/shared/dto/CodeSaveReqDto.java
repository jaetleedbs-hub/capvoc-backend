package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 저장 요청")
public class CodeSaveReqDto {
    @Schema(description = "회원사유형", required = true)
    private String cmpyTypeCd;
    @Schema(description = "코드그룹", required = true)
    private String cdGroup;
    @Schema(description = "코드", required = true)
    private String cd;
    @Schema(description = "로케일", required = true)
    private String locale;
    @Schema(description = "코드명", required = true)
    private String cdNm;
    @Schema(description = "코드구분")
    private String cdSe;
    @Schema(description = "사용여부")
    private String useYn;
    @Schema(description = "정렬일련번호")
    private Integer sortSn;
}
