package buttle.apptpl.common.framework.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrgrmAuthrt implements Serializable {
	private static final long serialVersionUID = 2229044148303606169L;
	private String httpMethod;
	private String urlAddrPtrn;
	private String authrtCd;
}
