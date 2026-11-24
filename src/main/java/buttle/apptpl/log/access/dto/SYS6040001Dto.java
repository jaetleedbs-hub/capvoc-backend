package buttle.apptpl.log.access.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 프로그램접속이력 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6040001Dto extends BaseDto {

    @Schema(description = "로그 시퀀스ID", type = "integer")
    private Integer logSid;

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "IP 주소", type = "string")
    private String ipAddr;

    @Schema(description = "Url 주소", type = "string")
    private String urlAddr;

    @Schema(description = "조회 시작일", type = "string")
    private String startDate;

    @Schema(description = "조회 종료일", type = "string")
    private String endDate;

} 