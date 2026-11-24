package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CmptUserMenuAuthrtPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "menu_id", nullable = false, length = 50)
    private String menuId;

    @Column(name = "usr_id", nullable = false, length = 255)
    private String userId;
} 