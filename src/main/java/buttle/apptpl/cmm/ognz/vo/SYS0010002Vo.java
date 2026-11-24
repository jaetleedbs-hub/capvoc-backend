package buttle.apptpl.cmm.ognz.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

/**
 * 조직관리 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0010002Vo {

    private String ognzCd;

    private String ognzNm;

    private String upOgnzCd;

    private BigDecimal ognzLvl;

    private BigDecimal sortSn;
} 