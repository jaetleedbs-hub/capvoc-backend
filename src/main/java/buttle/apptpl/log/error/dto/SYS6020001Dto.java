package buttle.apptpl.log.error.dto;

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
/**
 * SYS6020 오류로그 조회 요청 DTO
 */
public class SYS6020001Dto extends BaseDto {

    @Schema(description = "로그 시퀀스ID", type = "integer")
    private Integer logSid;

    @Schema(description = "서비스 코드 (DESK·CONNECT·SEARCH·CHAT·ADMIN·BATCH)", type = "string")
    private String svcCd;

    @Schema(description = "등급 (ERROR·WARN)", type = "string")
    private String sevCd;

    @Schema(description = "실패 종류 (EXCEPTION·DEPENDENCY·CONFIG·DEGRADED·REJECTED)", type = "string")
    private String failKindCd;

    @Schema(description = "회원사ID", type = "string")
    private String cmpyId;

    @Schema(description = "대상 (부분일치)", type = "string")
    private String subject;

    @Schema(description = "조회 시작일", type = "string")
    private String startDate;

    @Schema(description = "조회 종료일", type = "string")
    private String endDate;

}
