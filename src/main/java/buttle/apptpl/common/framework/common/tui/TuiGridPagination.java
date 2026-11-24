package buttle.apptpl.common.framework.common.tui;

import lombok.Data;
import micoai.corebase.util.pagination.CorePaginationInfo;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
public class TuiGridPagination<T> implements Serializable {
	private static final long serialVersionUID = -2171082910033168354L;
	private Boolean result = true;
	private TuiGridData<T> data = null;

    public TuiGridPagination(CorePaginationInfo info, List<T> list) {
    	this.result = true;
    	this.data = new TuiGridData<>();
    	this.data.setContents(list);
    	this.data.getPagination().setPage(info.getPage());
    	this.data.getPagination().setTotalCount(info.getTotalItems());
    }

	public TuiGridPagination(Page<T> page) {
		this.result = true;
		this.data = new TuiGridData<>();
		this.data.setContents(page.getContent());
		this.data.getPagination().setPage(page.getPageable().getPageNumber()+1);
		this.data.getPagination().setTotalCount(page.getTotalElements());
	}

}
