package buttle.apptpl.api.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 권한(사용자등급) 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AuthrtResDto {
    
    private String authrtCd;        // 권한코드
    private String authrtNm;        // 권한명
    private String authrtSe;        // 권한구분
    private String useYn;           // 사용여부
}
