package buttle.apptpl.api.shared.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BO403SCodeSearchReqVo {
    private String cmpyTypeCd;
    private String cdGroup;
    private String cd;
    private String cdNm;
    private String locale;
    private String useYn;
}
