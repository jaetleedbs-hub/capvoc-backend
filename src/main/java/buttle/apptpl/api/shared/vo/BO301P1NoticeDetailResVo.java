package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BO301P1NoticeDetailResVo {
    private String pstId;
    private String bbsId;
    private String ttl;
    private String pstCn;
    private String ntcYn;
    private String viewUseYn;
    private String atchFileGroupId;
    private Integer inqCnt;
    private String regDt;
    private String rgtrId;
}
