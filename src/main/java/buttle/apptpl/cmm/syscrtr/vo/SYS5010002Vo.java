package buttle.apptpl.cmm.syscrtr.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS5010002Vo {

    private String sysCrtrCd;
    private String sysCrtrCdNm;
    private String sysCrtrCdVl;
    private String sysCrtrTypeCd;
    private String rmrkCn;
    private String useYn;

} 