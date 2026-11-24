package buttle.apptpl.cmm.cd.vo;

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
public class SYS5040001Vo {

    private String cdGroup;
    private String cdGroupNm;
    private String useYn;
    private String cdPlcusCd;
    private String locale;
} 