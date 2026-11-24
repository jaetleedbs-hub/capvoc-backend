package buttle.apptpl.common.framework.common.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LocaleInfo implements Serializable {
	private static final long serialVersionUID = 7025593309626565757L;
	protected String locale;
	private String localeNm;
}
