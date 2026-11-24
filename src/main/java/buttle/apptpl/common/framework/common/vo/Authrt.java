package buttle.apptpl.common.framework.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authrt implements Serializable {
    @Serial
    private static final long serialVersionUID = 2173269107776477454L;
    private String mbrId;
    private String authrtCd;
    private String authrtNm;
}
