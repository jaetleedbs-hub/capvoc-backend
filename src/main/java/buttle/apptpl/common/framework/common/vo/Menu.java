package buttle.apptpl.common.framework.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Menu implements Serializable {

	@Serial
	private static final long serialVersionUID = 4488033540411248531L;

	private String sysCd;
	private String menuId;
	private String menuNm;
	private String urlAddr;
	private Character npagYn;
	private String upMenuId;
	private Integer sortSn;
	private String rmrkCn;
	private List<Menu> children;
}
