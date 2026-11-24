package buttle.apptpl.cmm.authrt.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사용자권한메뉴목록 결과 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS3020003Vo {

    private String saveSeYn;

    private String menuId;
    
    private String authrtCd;

    private String menuNm;

    private String urlAddr;

    private String npagYn;

    private String upMenuId;

    private String upMenuIdNm;

    private Integer sortSn;
} 