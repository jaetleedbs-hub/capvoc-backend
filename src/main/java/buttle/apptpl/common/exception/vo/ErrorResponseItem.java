package buttle.apptpl.common.exception.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseItem implements Serializable {
	private static final long serialVersionUID = -4264359674405906569L;
	private String field;
	private String message;
}
