package buttle.apptpl.cmm.authrt.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사용자권한코드관리 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3010002Vo {

    private String authrtCd;

    private String authrtNm;

    private String authrtSe;

    private String ognzInqRangCd;

    private String ognzInqRangCdNm;

    private String bscScrnMenuId;

    private String bscScrnMenuIdNm;

    private Integer sortSn;

    private String useYn;

    private String mdfcnDt;
} 