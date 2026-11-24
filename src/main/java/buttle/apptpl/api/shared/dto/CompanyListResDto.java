package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 회원사 목록 응답 DTO (Selectbox용)
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Schema(description = "회원사 목록 응답 (Selectbox용)")
public class CompanyListResDto extends BaseDto {
    
    @Schema(description = "회원사ID (value)", type = "string")
    private String value;
    
    @Schema(description = "회원사명 (name)", type = "string")
    private String name;

    @Schema(description = "회원사 유형명 (sub)", type = "string")
    private String sub;
}
