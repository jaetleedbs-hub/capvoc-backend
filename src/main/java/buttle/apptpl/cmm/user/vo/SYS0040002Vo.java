package buttle.apptpl.cmm.user.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

/**
 * 조직목록 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0040002Vo {

    private String ognzCd;
    private String ognzNm;
    private BigDecimal ognzLvl;
    private String upOgnzCd;
    private BigDecimal sortSn;
} 