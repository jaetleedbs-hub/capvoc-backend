package buttle.apptpl.cmm.userip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 사용자IP관리 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0080001Dto extends BaseDto {

    @Schema(description = "로그인 IP", type = "string")
    private String lgnIp;

    @Schema(description = "내선 번호", type = "string")
    private String extNo;
}