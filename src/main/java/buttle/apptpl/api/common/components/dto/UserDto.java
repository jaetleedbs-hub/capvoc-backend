package buttle.apptpl.api.common.components.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;
import lombok.experimental.SuperBuilder; import buttle.apptpl.common.data.BaseDto;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserDto extends BaseDto {
    private String userId;
    private String userNm;
    private String ognzCd;
    private String ognzNm;
    private String authrtCd;
    private String authrtCdNm;
    private String userTypeCd;
    private String userTypeCdNm;
    private String mblTelNo;
    private String emlAddr;
    private byte[] userPhoto;
    private String useYn;
}
