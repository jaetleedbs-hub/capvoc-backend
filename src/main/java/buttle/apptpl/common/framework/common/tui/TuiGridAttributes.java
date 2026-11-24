package buttle.apptpl.common.framework.common.tui;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class TuiGridAttributes implements Serializable {
	private static final long serialVersionUID = -6007478887919971125L;
	private Boolean checkDisabled;
	private Boolean checked;
	private Boolean disabled;
	private Long rowNum;
}
