package buttle.apptpl.cmm.pswdplcy.vo;

import java.math.BigDecimal;

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
public class SYS5020002Vo {

    private BigDecimal pswdPlcyId;
    private BigDecimal pswdChgCycCnt;
    private BigDecimal lgnTryPsbltyCnt;
    private String   pswdReuseYn;
    private BigDecimal pswdMinLen;
    private String pswdUcReqYn;
    private String pswdLcReqYn;
    private String pswdNumReqYn;
    private String pswdSpcCharReqYn;
    private String pswdDenyChar;

} 