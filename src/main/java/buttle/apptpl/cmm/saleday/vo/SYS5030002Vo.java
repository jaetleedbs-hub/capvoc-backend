package buttle.apptpl.cmm.saleday.vo;

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
public class SYS5030002Vo {

    private String saleDay;
    private String ognzCd;
    private String ognzCdNm;
    private String hldySeCd;
    private String hldySeCdNm;
    private String wdaySeCd;
    private String wdaySeCdNm;
    private String rmrk;

} 