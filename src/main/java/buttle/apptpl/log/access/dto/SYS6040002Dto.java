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
 * 프로그램접속이력 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS6040002Dto extends BaseDto {

    // [2026-08-20] Integer 였다. N-18 되돌림으로 원천이 cdm_own.conn_log 로
    //  바뀌면서 키가 <숫자 시퀀스>에서 <문자열 log_id>(예: alog-189cf77e…)가 됐는데
    //  DTO 만 Integer 로 남아, ModelMapper 가 String→Integer 변환에 실패하며
    //  SYS6040 조회가 통째로 500 이었다. VO(SYS6040002Vo.logSid)는 이미 String 이다.
    //  화면은 이 값을 쓰지 않는다(그리드 표시 컬럼에 없음).
    @Schema(description = "로그 ID", type = "string")
    private String logSid;

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "사용자 이름", type = "string")
    private String userNm;

    @Schema(description = "IP 주소", type = "string")
    private String ipAddr;

    // @Schema(description = "프로그램 ID", type = "string")
    // private String prgrmId;

    @Schema(description = "http 메소드", type = "string")
    private String httpMethod;

    @Schema(description = "URL 주소", type = "string")
    private String urlAddr;
   
    @Schema(description = "로그 일시", type = "string")
    private String logDt;

} 