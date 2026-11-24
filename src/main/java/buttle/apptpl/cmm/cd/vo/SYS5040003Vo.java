package buttle.apptpl.cmm.cd.vo;

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
public class SYS5040003Vo {

    private String cdGroup;
    private String cd;
    private String locale; /* 2026.11.20 추가 */
    private String cdNm;
    private String useYn;
} 