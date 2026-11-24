package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사용자 목록 응답 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserListResVo {
    
    private Integer rownum;
    private String userId;
    private String userNm;
    private String deptCd;
    private String deptNm;
    private String userGradeCd;
    private String userGradeNm;
    private String lgnYn;
    private String lgnIp;
    private String wrkStatCd;      // 근태코드
    private String wrkStatNm;      // 근태상태명
    private String jncmpYmd;       // 입사일자
    private String rsgntnYmd;      // 퇴사일자
    private String useStartYmd;    // 사용시작일
    private String useEndYmd;      // 사용종료일
}
