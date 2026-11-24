package buttle.apptpl.cmm.loginuser.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 로그인사용자정보 응답
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM1100001Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "조직명", type = "string")
    private String ognzNm;

    @Schema(description = "권한명", type = "string")
    private String authrtNm;

    @Schema(description = "사용자 사진", type = "string")
    private String userPhoto;
} 