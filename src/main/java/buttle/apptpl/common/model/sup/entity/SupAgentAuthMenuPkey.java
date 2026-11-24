package buttle.apptpl.common.model.sup.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupAgentAuthMenuPkey implements Serializable {

    private Integer authId;

    private String menuId;
} 