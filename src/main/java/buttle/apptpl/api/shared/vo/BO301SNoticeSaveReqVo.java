package buttle.apptpl.api.shared.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BO301SNoticeSaveReqVo {
    private String pstId;
    private String bbsId;
    private String ttl;
    private String pstCn;
    private String ntcYn;
    private String topStdYn;    
    private String viewUseYn;
    private String bbsStYmd;
    private String bbsStTm;
    private String bbsEdYmd;
    private String bbsEdTm;
    private String atchFileGroupId;
    private String gdsUserId;
}
