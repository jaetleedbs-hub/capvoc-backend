package buttle.apptpl.common.framework.common.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import micoai.corebase.util.CoreUtils;
import buttle.apptpl.common.model.cmpt.entity.CmptAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecUser implements Serializable {
	private static final long serialVersionUID = 2394862584841987303L;
	private String userId;
	private String lgnId;
	private String userNm;
	private String ognzCd; // 조직 코드
	private String authrtCd; // 권한 코드
	private String userTypeCd; // 사용자 유형 코드
	private String dscsnTaskSeCd; // 상담 업무 구분 코드
	private String ognzInqRangCd; // 조직 문의 범위 코드
	private String empNo; // 사원 번호
	private String authrtSe; // 권한 레벨
	private String hrOgnzCd; // 회사 조직 코드
	private String extNo; // 내선 번호
	private String lgnIp;

  // CTI 관련 필드
  private String ctiUseYn; // CTI 사용 여부
	private String ctiAutoLgnYn; // CTI 자동 로그인 여부
	private String ctiId; // CTI ID

	private Boolean enabled = true;
	private List<String> authrtCdList;
	
	public void setRoles(Collection<? extends GrantedAuthority> authorityList) {
		if (this.authrtCdList == null) {
			this.authrtCdList = new ArrayList<>();
		}

		for (GrantedAuthority authority: authorityList) {
			String role = authority.toString();
			this.authrtCdList.add(role);
		}
	}

}
