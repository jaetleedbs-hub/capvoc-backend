package buttle.apptpl.api.shared.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BO403SCodeSaveReqVo {
    private String cmpyTypeCd;
    private String cdGroup;
    private String cd;
    private String locale;
    private String cdNm;
    private String cdSe;
    private String useYn;
    private Integer sortSn;
}
