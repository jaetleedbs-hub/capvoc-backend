package buttle.apptpl.cmm.authrt.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사용자별예외권한 응답 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3030003Vo {

    private String menuId;

    private String menuNm;

    private String authrtGrntSeCd;

    private String authrtGrntSeCdNm;
} 