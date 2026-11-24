package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 권한(사용자등급) 응답 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AuthrtResVo {
    
    private String authrtCd;
    private String authrtNm;
    private String authrtDesc;
    private String useYn;
    private Integer sortOrdr;

}
