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
 * 로그인사용자정보 이미지 등록 요청
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM1100002Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "사용자 사진", type = "string")
    private String userPhoto;
} 