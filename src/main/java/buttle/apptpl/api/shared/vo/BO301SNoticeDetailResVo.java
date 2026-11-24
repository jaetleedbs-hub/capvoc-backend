package buttle.apptpl.api.shared.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BO301SNoticeDetailResVo {
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
    private String bbsStDt;  // 게시시작일시 (YYYY-MM-DD HH24:MI)
    private String bbsEdDt;  // 게시종료일시 (YYYY-MM-DD HH24:MI)
    private String atchFileGroupId;
    private Integer inqCnt;  // 조회수
    private String regId;
    private String regNm;    // 등록자명 (usr.usr_nm)
    private String regDt;
}
