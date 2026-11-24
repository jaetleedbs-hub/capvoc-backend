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
public class SYS0030002Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "사용자 명", type = "string")
    private String userNm;

    @Schema(description = "사용자 등급", type = "string")
    private String authrtCd;

    @Schema(description = "사용자 유형", type = "string")
    private String userTypeCd;

    @Schema(description = "소속부서코드", type = "string")
    private String ognzCd;

    @Schema(description = "로그인 여부", type = "string")
    private String lgnYn;

    @Schema(description = "로그인 시도 수", type = "number")
    private String lgnTryCnt;

    @Schema(description = "로그인 IP", type = "string")
    private String lgnIp;

    @Schema(description = "비밀번호 변경일시", type = "string")
    private String pswdChgDt;

    @Schema(description = "입사 일자", type = "string")
    private String jncmpYmd;

    @Schema(description = "퇴사 일자", type = "string")
    private String rsgntnYmd;

    @Schema(description = "자택 전화 번호", type = "string")
    private String homeTelNo;

    @Schema(description = "휴대 전화 번호", type = "string")
    private String mblTelNo;

    @Schema(description = "이메일 주소", type = "string")
    private String emlAddr;

    @Schema(description = "사용자 사진", type = "string")
    private String userPhoto;

    @Schema(description = "사용 여부", type = "string")
    private String useYn;

    @Schema(description = "CTI 사용여부", type = "string")
    private String ctiUseYn;

    @Schema(description = "CTI 자동 로그인 여부", type = "string")
    private String ctiAutoLgnYn;

    @Schema(description = "CTI ID", type = "string")
    private String ctiId;

    @Schema(description = "사원번호", type = "string")
    private String empNo;

    @Schema(description = "내선 번호", type = "string")
    private String extNo;

    @Schema(description = "출근 시간", type = "string") 
    private String workTm;
    
    @Schema(description = "상담 업무 구분 코드", type = "string")
    private String dscsnTaskSeCd;
    
    @Schema(description = "콜백 업무 사용 여부", type = "string")
    private String callbakTaskUseYn;

    @Schema(description = "조직 이름", type = "string")
    private String ognzNm;
}