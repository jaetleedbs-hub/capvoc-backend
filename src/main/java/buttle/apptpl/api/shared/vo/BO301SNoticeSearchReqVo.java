package buttle.apptpl.api.shared.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BO301SNoticeSearchReqVo {
    private String bbsId;
    private String ttl;
    private String viewUseYn;
    private String bbsStYmd;
    private String bbsEdYmd;
}
