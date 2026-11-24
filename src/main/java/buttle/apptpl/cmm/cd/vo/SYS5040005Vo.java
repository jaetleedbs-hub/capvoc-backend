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
public class SYS5040005Vo {

    private String cd;
    private String cdGroup;
    private String locale;
    private String rfrncCd;
    private String rfrncNm;
    private String rfrncVl;
} 