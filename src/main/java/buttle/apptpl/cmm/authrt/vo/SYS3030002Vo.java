package buttle.apptpl.cmm.authrt.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사용자별권한 응답 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3030002Vo {

    private String userId;

    private String userNm;

    private String ognzCd;

    private String ognzNm;

    private String authrtCd;

    private String authrtCdNm;
} 