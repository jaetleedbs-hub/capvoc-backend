package buttle.apptpl.cmm.authrt.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * SYS3030 프로그램 조회 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3030004Vo {

    private String userId;         /* 사용자 ID */
    private String prgrmId;        /* 프로그램 ID */
    private String prgrmNm;        /* 프로그램 명 */
    private String httpMethod;     /* http 메소드 */
    private String urlAddrPtrn;    /* URL 주소 패턴 */
    private Integer sortSn;        /* 정렬 일련번호 */
    private String authrtGrntSeCd; /* 권한 부여 구분 코드 */
    private String authrtGrntSeCdNm; /* 권한 부여 구분 코드 명 */
    private String prgrmIdOrNm;    /* 프로그램 검색 조건 */
    private String rgtrId;         /* 등록자 ID */
    private String regDt;          /* 등록일시 */
    private String mdfrId;         /* 수정자 ID */
    private String mdfcnDt;        /* 수정일시 */
} 