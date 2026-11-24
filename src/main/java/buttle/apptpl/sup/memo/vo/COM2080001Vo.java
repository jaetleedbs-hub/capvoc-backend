package buttle.apptpl.sup.memo.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 슬라이드프레임_메모 등록 요청
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM2080001Vo {

    private String userId;
    private Integer memoCtgySid;
    private Integer memoSid;
    private String memoCtgyNm;
    private Integer sortSn;
    private String rgtrId;
    private String mdfrId;
} 