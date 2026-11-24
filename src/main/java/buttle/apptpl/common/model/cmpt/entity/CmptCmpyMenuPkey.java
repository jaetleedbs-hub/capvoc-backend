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
public class CmptCmpyMenuPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 회원사유형
     */
    @Column(name = "cmpy_type_cd", nullable = false, length = 20)
    private String cmpyTypeCd;

    /**
     * 메뉴 ID
     */
    @Column(name = "menu_id", nullable = false, length = 50)
    private String menuId;
}
