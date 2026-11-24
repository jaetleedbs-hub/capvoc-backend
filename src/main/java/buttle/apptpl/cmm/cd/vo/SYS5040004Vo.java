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
public class SYS5040004Vo {

    private String cd;
    private String cdGroup;
    private String locale;
    private String cdNm;
    private String cdSe;
    private String useYn;
    private Integer sortSn;
    private String rgtrId;
    private String rgtrIdNm;
    private String regDt;
    private String mdfrId;
    private String mdfrIdNm;
    private String mdfcnDt;
} 