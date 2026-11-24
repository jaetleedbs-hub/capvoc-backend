package buttle.apptpl.cmm.loginuser.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 로그인사용자정보 결과
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM1100002Vo {

    private String userId;
    private String ognzNm;
    private String authrtNm;
    private String userPhoto;
} 