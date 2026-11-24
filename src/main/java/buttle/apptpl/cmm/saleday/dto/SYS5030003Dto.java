package buttle.apptpl.cmm.saleday.dto;

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
@Schema(description = "영업일 생성 응답")
public class SYS5030003Dto extends BaseDto {

    @Schema(description = "생성 결과", type = "Boolean")
    private Boolean result;

    @Schema(description = "생성 결과 메시지", type = "String")
    private String message;
}
