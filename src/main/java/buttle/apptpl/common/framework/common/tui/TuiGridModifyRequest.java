package buttle.apptpl.common.framework.common.tui;

import lombok.Data;

import java.util.List;

@Data
public class TuiGridModifyRequest<T> {
	private List<T> createdRows;
	private List<T> deletedRows;
	private List<T> updatedRows;
}
