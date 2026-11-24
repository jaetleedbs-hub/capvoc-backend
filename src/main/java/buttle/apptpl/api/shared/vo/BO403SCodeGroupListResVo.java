package buttle.apptpl.api.shared.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BO403SCodeGroupListResVo {
    private String cmpyTypeCd;
    private String cdGroup;
    private String cdGroupNm;
    private String rmrkCn;
    private String useYn;
    private String cdPlcusCd;
    private String regDt;
}
