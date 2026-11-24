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
public class CmptCmpyCdPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 회원사유형
     */
    @Column(name = "cmpy_type_cd", nullable = false, length = 20)
    private String cmpyTypeCd;

    /**
     * 코드그룹
     */
    @Column(name = "cd_group", nullable = false, length = 20)
    private String cdGroup;

    /**
     * 코드
     */
    @Column(name = "cd", nullable = false, length = 20)
    private String cd;

    /**
     * 로케일
     */
    @Column(name = "locale", nullable = false, length = 20)
    private String locale;
}
