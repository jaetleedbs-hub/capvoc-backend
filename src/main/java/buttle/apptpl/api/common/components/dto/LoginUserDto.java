package buttle.apptpl.api.common.components.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.framework.common.vo.SecUser;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LoginUserDto extends SecUser {
	private String userId; // 사용자 ID
	private String userNm; // 사용자 명
	private String ognzCd; // 조직 코드
	private String ognzNm; // 조직 명
	private String bscScrnMenuId; // 기본 화면 메뉴 아이디
	private String ctiUseYn; // CTI 사용 여부
	private String ctiAutoLgnYn; // CTI 자동 로그인 여부
	private String ctiId; // CTI ID
	private String authrtCd; // 권한 코드
	private String userTypeCd; // 사용자 유형 코드
	private String dscsnTaskSeCd; // 상담 업무 구분 코드
	private String ognzInqRangCd; // 조직 문의 범위 코드
	private String empNo; // 사원 번호
	private String authrtSe; // 권한 레벨
	private String hrOgnzCd; // 회사 조직 코드
	private String extNo; // 내선 번호
	private String lgnIp; // 로그인 IP
}
