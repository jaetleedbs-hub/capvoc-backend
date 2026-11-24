package buttle.apptpl.cmm.pswdplcy.dto;

import java.math.BigDecimal;

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
@Schema(description = "비밀번호정책정보 응답")
public class SYS5020002Dto extends BaseDto {

    @Schema(description = "비밀번호정책아이디", type = "BigDecimal")
    private BigDecimal pswdPlcyId;

    @Schema(description = "비밀번호 변경 주기 수", type = "BigDecimal")
    private BigDecimal pswdChgCycCnt;

    @Schema(description = "로그인 시도 가능 수", type = "BigDecimal")
    private BigDecimal lgnTryPsbltyCnt;

    @Schema(description = "비밀번호 재사용 여부", type = "String")
    private String pswdReuseYn;

    @Schema(description = "비밀번호 최소 길이", type = "BigDecimal")
    private BigDecimal pswdMinLen;

    @Schema(description = "비밀번호 대문자 요구 여부", type = "String")
    private String pswdUcReqYn;

    @Schema(description = "비밀번호 소문자 요구 여부", type = "String")
    private String pswdLcReqYn;

    @Schema(description = "비밀번호 숫자 요구 여부", type = "String")
    private String pswdNumReqYn;

    @Schema(description = "비밀번호 특수 문자 요구 여부", type = "String")
    private String pswdSpcCharReqYn;

    @Schema(description = "비밀번호 금지 문자", type = "String")
    private String pswdDenyChar;

}