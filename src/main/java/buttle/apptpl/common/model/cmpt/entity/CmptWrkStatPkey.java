package buttle.apptpl.common.model.cmpt.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmptWrkStatPkey implements Serializable {

    private String workYmd;

    private String userId;
} 