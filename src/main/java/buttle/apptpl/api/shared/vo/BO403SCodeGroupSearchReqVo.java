package buttle.apptpl.api.shared.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BO403SCodeGroupSearchReqVo {
    private String cmpyTypeCd;
    private String cdGroup;
    private String cdGroupNm;
    private String useYn;
}
