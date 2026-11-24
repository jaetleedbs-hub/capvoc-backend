package buttle.apptpl.api.shared.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BO301SNoticeListResVo {
    private String pstId;
    private String bbsId;
    private String ttl;
    private String pstCn;
    private String ntcYn;
    private String viewUseYn;
    private String topStdYn;  // 상단고정 여부
    private String bbsStYmd;
    private String bbsStTm;
    private String bbsEdYmd;
    private String bbsEdTm;
    private String bbsStDt;  // 게시시작일시 (YYYY-MM-DD HH24:MI)
    private String bbsEdDt;  // 게시종료일시 (YYYY-MM-DD HH24:MI)
    private Integer inqCnt;
    private String regId;
    private String regNm;    // 등록자명 (usr.usr_nm)
    private String regDt;
}
