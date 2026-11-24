package buttle.apptpl.common.security.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import buttle.apptpl.sup.bookmark.dto.COM2070008Dto;
import buttle.apptpl.api.common.components.dto.LoginUserDto;
import buttle.apptpl.cmm.menu.vo.SYS1020002Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptPrgrmEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessVo implements Serializable {
	private static final long serialVersionUID = -3048831223292929127L;
	LoginUserDto user;
	String redirectUrl;
	List<Map<String, Object>> codeList;
	List<SYS1020002Vo> menuList;
	List<CmptPrgrmEntity> prgrmList;
	List<COM2070008Dto> bookmarkList;
	Map<String, String> frontendSysBaseInfo;
}
