package buttle.apptpl.auth.password.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 비밀번호변경 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM0020001Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "변경 일시", type = "string")
    private String chgDt;

    @Schema(description = "비밀번호", type = "string")
    private String pswd;

    @Schema(description = "현재 비밀번호", type = "string")
    private String currentPassword;

    @Schema(description = "새 비밀번호", type = "string")
    private String newPassword;

    @Schema(description = "새 비밀번호 확인", type = "string")
    private String confirmPassword;

    @Schema(description = "메시지", type = "string")
    private String message;
} 