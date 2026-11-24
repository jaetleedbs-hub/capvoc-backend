package buttle.apptpl.common.framework.common.tui;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class TuiGridData<T> implements Serializable {
	@Serial
	private static final long serialVersionUID = 7038776062084880397L;
	private  List<T> contents;
	private Pagination pagination;

	public TuiGridData() {
		this.pagination = new Pagination();
	}

	@Data
	public static class Pagination implements Serializable {
		@Serial
		private static final long serialVersionUID = -6552177758842160188L;
		private Integer page;
		private Long totalCount;
	}
}
