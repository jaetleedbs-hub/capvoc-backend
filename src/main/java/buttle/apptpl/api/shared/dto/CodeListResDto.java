package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "코드 목록 응답")
public class CodeListResDto {
    @Schema(description = "회원사유형")
    private String cmpyTypeCd;
    @Schema(description = "코드그룹")
    private String cdGroup;
    @Schema(description = "코드")
    private String cd;
    @Schema(description = "로케일")
    private String locale;
    @Schema(description = "코드명")
    private String cdNm;
    @Schema(description = "코드구분")
    private String cdSe;
    @Schema(description = "사용여부")
    private String useYn;
    @Schema(description = "정렬일련번호")
    private Integer sortSn;
    @Schema(description = "등록일시")
    private String regDt;
}
