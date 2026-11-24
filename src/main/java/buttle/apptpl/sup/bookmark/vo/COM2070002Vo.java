package buttle.apptpl.sup.bookmark.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 슬라이드프레임_즐겨찾기 등록 결과
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM2070002Vo {

    private String userId;
    private Integer bokmkCtgySid;
    private String bokmkMenuId;
    private Integer sortSn;
} 