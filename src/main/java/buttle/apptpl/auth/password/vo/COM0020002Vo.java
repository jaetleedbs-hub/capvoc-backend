package buttle.apptpl.auth.password.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

/**
 * 비밀번호변경 응답 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM0020002Vo {

    private String userId;

    private String pswd;

    private BigDecimal lgnTryCnt;
} 