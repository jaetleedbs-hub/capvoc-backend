package buttle.apptpl.cmm.prgrm.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 프로그램관리 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3040001Vo {

    private String prgrmId;
    private String prgrmNm;
    private String httpMethod;
    private String urlAddrPtrn;
    private Integer sortSn;
    private String prgrmIdOrNm;
    private String rgtrId;
    private String regDt;
    private String mdfrId;
    private String mdfcnDt;
} 