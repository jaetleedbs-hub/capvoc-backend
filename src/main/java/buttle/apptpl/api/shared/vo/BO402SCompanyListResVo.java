package buttle.apptpl.api.shared.vo;

import lombok.*;

/**
 * BO402_S 회원사 목록 응답 VO (Selectbox용)
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BO402SCompanyListResVo {
    
    private String value;
    private String name;
    private String sub;
}
