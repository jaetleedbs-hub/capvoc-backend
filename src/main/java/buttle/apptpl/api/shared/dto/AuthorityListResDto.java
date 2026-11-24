package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 권한 목록 응답 DTO (Selectbox용)
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Schema(description = "권한 목록 응답 (Selectbox용)")
public class AuthorityListResDto extends BaseDto {
    
    @Schema(description = "권한코드 (value)", type = "string")
    private String value;
    
    @Schema(description = "권한명 (name)", type = "string")
    private String name;
}
