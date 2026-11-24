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
public class SYS5040002Vo {

    private String cdGroup;
    private String cdGroupNm;
    private String rmrkCn;
    private String useYn;
    private String cdPlcusCd;
    private String cdPlcusCdNm;
    private String rgtrId;
    private String rgtrIdNm;
    private String regDt;
    private String mdfrId;
    private String mdfrIdNm;
    private String mdfcnDt;
} 