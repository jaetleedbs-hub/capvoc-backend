package buttle.apptpl.common.security.bruteforce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptVo implements Serializable {
	private static final long serialVersionUID = -2513228588319382866L;
	String ip;
	boolean locked;
	int loginTryCnt;
	Date loginTryDt;
	Date lockRmvDt;
}
