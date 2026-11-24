package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serial;
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
public class CmptRfrncCdPkey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "cmmn_cd", length = 20)
    private String cd;

    @Column(name = "cd_grop_cd", length = 50)
    private String cdGroup;

    @Column(name = "lcal_cd", length = 20)
    private String locale;

    @Column(name = "ref_cd", length = 20)
    private String rfrncCd;
} 