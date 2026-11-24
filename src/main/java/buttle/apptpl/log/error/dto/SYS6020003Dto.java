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
 * SYS6020 오류로그 상세 응답 DTO
 */
public class SYS6020003Dto extends BaseDto {

    @Schema(description = "로그 시퀀스ID", type = "integer")
    private Integer logSid;

    @Schema(description = "로그 일시", type = "string")
    private String logDt;

    @Schema(description = "서비스 코드", type = "string")
    private String svcCd;

    @Schema(description = "등급", type = "string")
    private String sevCd;

    @Schema(description = "실패 종류", type = "string")
    private String failKindCd;

    @Schema(description = "회원사ID", type = "string")
    private String cmpyId;

    @Schema(description = "서버ID", type = "string")
    private String svrId;

    @Schema(description = "대상", type = "string")
    private String subject;

    @Schema(description = "오류 코드", type = "string")
    private String errCd;

    @Schema(description = "사유(전문)", type = "string")
    private String errCn;

    @Schema(description = "원본 스키마", type = "string")
    private String srcSchema;

    @Schema(description = "원본 로그ID", type = "string")
    private String srcLogId;

}
