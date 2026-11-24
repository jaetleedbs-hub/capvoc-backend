package buttle.apptpl.cmm.ognz.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

/**
 * 조직조회 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0020002Vo {

    private String ognzCd;

    private String ognzNm;

    private BigDecimal ognzLvl;

    private String upOgnzCd;

    private String ognzPathNm;

    private BigDecimal sortSn;

    private String useYn;
} 