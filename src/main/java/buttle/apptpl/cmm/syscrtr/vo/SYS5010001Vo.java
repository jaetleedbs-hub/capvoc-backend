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
public class SYS5010001Vo {

    private String sysCrtrCd;
    private String sysCrtrCdOrNm;
    private String sysCrtrTypeCd;
    private String useYn;
} 