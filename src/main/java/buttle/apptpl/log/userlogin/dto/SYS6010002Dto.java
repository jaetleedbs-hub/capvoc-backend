package buttle.apptpl.log.userlogin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;
import java.math.BigDecimal;

/**
 * 사용자로그인이력 조회 결과
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6010002Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "사용자 명", type = "string")
    private String userNm;

    @Schema(description = "조직 명", type = "string")
    private String ognzNm;

    @Schema(description = "로그인 일자", type = "string")
    private String lgnYmd;

    @Schema(description = "로그인 IP", type = "string")
    private String lgnIp;

    @Schema(description = "로그인 일시", type = "string")
    private String lgnDt;

    @Schema(description = "로그아웃 일시", type = "string")
    private String lgtDt;

    @Schema(description = "로그인 수", type = "number")
    private BigDecimal lgnCnt;
} 