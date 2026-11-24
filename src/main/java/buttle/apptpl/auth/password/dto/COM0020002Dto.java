package buttle.apptpl.auth.password.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;
import java.math.BigDecimal;

/**
 * 비밀번호변경 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM0020002Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "로그인 시도 수", type = "number")
    private BigDecimal lgnTryCnt;

    @Schema(description = "메시지", type = "string")
    private String message;

    /**
     * 결과 코드(기계 판독용). 프론트가 한글 메시지 부분일치 대신 이 코드로 성공/실패·포커스 대상을 판정한다.
     * 성공: {@code SUCCESS}. 검증 실패: {@code SAME_AS_CURRENT}, {@code MISSING_USER_ID},
     * {@code MISSING_CURRENT}, {@code MISSING_NEW}, {@code MISSING_CONFIRM}, {@code CONFIRM_MISMATCH},
     * {@code USER_NOT_FOUND}.
     */
    @Schema(description = "결과 코드", type = "string")
    private String resultCode;
} 