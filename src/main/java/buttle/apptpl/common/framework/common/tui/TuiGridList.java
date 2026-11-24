package buttle.apptpl.common.framework.common.tui;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TuiGridList<T> implements Serializable {
	private static final long serialVersionUID = -2171082910033168354L;
	private Boolean result = true;
	private TuiGridData<T> data = null;

    public TuiGridList(List<T> list) {
    	this.result = true;
    	this.data = new TuiGridData<>();
    	this.data.setContents(list);
    }

}
