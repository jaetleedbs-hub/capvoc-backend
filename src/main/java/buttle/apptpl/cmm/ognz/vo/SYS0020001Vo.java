package buttle.apptpl.cmm.ognz.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 조직조회 조회 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0020001Vo {

    private String ognzCd;

    private String ognzCdOrNm;

    private String useYn;

    private String authrt;
} 