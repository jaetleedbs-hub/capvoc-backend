package buttle.apptpl.cmm.prgrm.vo;

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
public class SYS3050003Vo {
    
    private String saveSeYn;    /* 저장구분YN */
    private String prgrmId;     /* 프로그램 ID */
    private String prgrmNm;     /* 프로그램 명 */
    private String authrtCd;    /* 권한 코드 */
    private String httpMethod;  /* http 메소드 */
    private String urlAddrPtrn; /* URL 주소 패턴 */
    private Integer sortSn;     /* 정렬순서 */
} 