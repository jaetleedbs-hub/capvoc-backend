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
public class SYS5030001Vo {

    private String fromSaleDay;
    private String toSaleDay;
    private String ognzCd;
    private String hldySeCd;
} 