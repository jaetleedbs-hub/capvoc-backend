package buttle.apptpl.cmm.user.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 사용자목록 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0040003Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "String")
    private String userId;

    @Schema(description = "사용자 명", type = "String")
    private String userNm;

    @Schema(description = "비밀번호", type = "String")
    private String pswd;

    @Schema(description = "조직 코드", type = "String")
    private String ognzCd;

    @Schema(description = "조직 경로 명", type = "String")
    private String ognzPathNm;

    @Schema(description = "권한 코드", type = "String")
    private String authrtCd;

    @Schema(description = "권한 코드 명", type = "String")
    private String authrtCdNm;

    @Schema(description = "로그인 여부", type = "String")
    private String lgnYn;

    @Schema(description = "로그인 시도 수", type = "BigDecimal")
    private BigDecimal lgnTryCnt;

    @Schema(description = "로그인 IP", type = "String")
    private String lgnIp;

    @Schema(description = "비밀번호 변경일시", type = "String")
    private String pswdChgDt;

    @Schema(description = "사용자 유형 코드", type = "String")
    private String userTypeCd;

    @Schema(description = "사용자 유형 코드 명", type = "String")
    private String userTypeCdNm;

    @Schema(description = "입사 일자", type = "String")
    private String jncmpYmd;

    @Schema(description = "퇴사 일자", type = "String")
    private String rsgntnYmd;

    @Schema(description = "자택 전화 번호", type = "String")
    private String homeTelNo;

    @Schema(description = "휴대 전화 번호", type = "String")
    private String mblTelNo;

    @Schema(description = "이메일 주소", type = "String")
    private String emlAddr;

    @Schema(description = "사용자 사진", type = "String")
    private String userPhoto;

    @Schema(description = "사용 여부", type = "String")
    private String useYn;

    @Schema(description = "CTI 사용여부", type = "String")
    private String ctiUseYn;

    @Schema(description = "CTI 자동 로그인 여부", type = "String")
    private String ctiAutoLgnYn;

    @Schema(description = "CTI ID", type = "String")
    private String ctiId;

    @Schema(description = "상담 업무 구분 코드", type = "String")
    private String dscsnTaskSeCd;

    @Schema(description = "상담 업무 구분 코드 명", type = "String")
    private String dscsnTaskSeCdNm;
}