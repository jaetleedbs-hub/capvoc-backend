package buttle.apptpl.cmm.user.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사용자찾기 요청 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0040001Vo {

    private String ognzCd;
    private String ognzCdOrNm;
    private String useYn;
    private String userIdOrNm;
    private String leaveRsgntnIncl;
    private String userId;
    private String authrt;
} 