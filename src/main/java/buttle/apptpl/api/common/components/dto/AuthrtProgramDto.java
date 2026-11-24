package buttle.apptpl.api.common.components.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AuthrtProgramDto {
    private String prgrmId;
    private String prgrmNm;
    private String urlAddrPtrn;
    private String httpMethod;
    private boolean isAuthrtGrnt = true;
}
