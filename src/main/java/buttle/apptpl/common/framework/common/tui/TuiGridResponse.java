package buttle.apptpl.common.framework.common.tui;

import lombok.Data;

import java.io.Serializable;

@Data
public class TuiGridResponse implements Serializable {
	private static final long serialVersionUID = 1874836372815018805L;
	private Boolean result = true;

	public static TuiGridResponse success() {
		TuiGridResponse res = new TuiGridResponse();
		res.setResult(true);
		return res;
	}

	public static TuiGridResponse fail() {
		TuiGridResponse res = new TuiGridResponse();
		res.setResult(false);
		return res;
	}
}
