package buttle.apptpl.common.framework.common.tui;

import lombok.Data;

import java.util.List;

@Data
public class TuiGridUpdateRequest<T> {
	private List<T> updatedRows;
}
