package buttle.apptpl.cmm.ognz.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

/**
 * 조직상세 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0020003Vo {

    private String ognzCd;

    private String ognzNm;

    private BigDecimal ognzLvl;

    private String upOgnzCd;

    private String ognzPathNm;

    private String hrOgnzCd;

    private String ognzRprsTelNo;

    private BigDecimal sortSn;

    private String useYn;
} 