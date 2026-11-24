package buttle.apptpl.cmm.ognz.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

/**
 * 조직관리 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0010003Vo {

    private String ognzCd;

    private String ognzNm;

    private BigDecimal ognzLvl;

    private String upOgnzCd;

    private String upOgnzCdNm;

    private String ognzPathNm;

    private String hrOgnzCd;

    private String ognzRprsTelNo;

    private BigDecimal sortSn;

    private String useYn;
} 