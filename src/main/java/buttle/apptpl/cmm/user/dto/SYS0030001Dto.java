package buttle.apptpl.cmm.user.dto;

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
public class SYS0030001Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "사용자 명", type = "string")
    private String userNm;

    @Schema(description = "조직 코드", type = "string")
    private String ognzCd;

    @Schema(description = "소속부서명", type = "string")
    private String ognzNm;

    @Schema(description = "권한 코드", type = "string")
    private String authrtCd;

    @Schema(description = "사용자 등급", type = "string")
    private String authrtNm;

    @Schema(description = "CTI 사용 여부", type = "string")
    private String ctiUseYn;

    @Schema(description = "사원번호", type = "string")
    private String empNo;

    @Schema(description = "사용 여부", type = "string")
    private String useYn;

    @Schema(description = "로그인 여부", type = "string")
    private String lgnYn;

    @Schema(description = "로그인 IP", type = "string")
    private String lgnIp;    

    @Schema(description = "입사 일자", type = "string")
    private String jncmpYmd;

    @Schema(description = "퇴사 일자", type = "string")
    private String rsgntnYmd;

    @Schema(description = "근태 코드", type = "string")
    private String wrkStatNm;
}