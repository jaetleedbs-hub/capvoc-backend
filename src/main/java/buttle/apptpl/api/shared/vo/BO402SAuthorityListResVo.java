package buttle.apptpl.api.shared.vo;

import lombok.*;

/**
 * BO402_S 권한 목록 응답 VO (Selectbox용)
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BO402SAuthorityListResVo {
    
    private String value;
    private String name;
}
