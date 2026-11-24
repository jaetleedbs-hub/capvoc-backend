package buttle.apptpl.common.model.cmpt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CmptMenuAuthrtPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "menu_id", nullable = false, length = 50)
    private String menuId;

    @Column(name = "auth_cd", nullable = false, length = 20)
    private String authrtCd;
} 