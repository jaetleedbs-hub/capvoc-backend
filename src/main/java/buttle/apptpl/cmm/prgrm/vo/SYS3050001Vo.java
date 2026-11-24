package buttle.apptpl.cmm.prgrm.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 프로그램권한 요청 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3050001Vo {

    private String prgrmIdOrNm;
    private String authrtCd;
    private Integer sortSn;
    private String httpMethod;
    private String urlAddrPtrn;
} 