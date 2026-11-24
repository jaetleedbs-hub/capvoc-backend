package buttle.apptpl.sup.memo.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 슬라이드프레임_메모 등록 결과
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM2080002Vo {

    private String userId;
    private Integer memoCtgySid;
    private Integer memoSid;
    private String memoTtlNm;
    private String memoCn;
    private String memoAlrmYn;
    private String memoAlrmDt;
    private Integer sortSn;
} 