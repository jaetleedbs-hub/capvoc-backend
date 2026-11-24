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
public class CmptCdPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "cmmn_cd", nullable = false, length = 20)
    private String cd;

    @Column(name = "cd_grop_cd", nullable = false, length = 50)
    private String cdGroup;

    @Column(name = "lcal_cd", nullable = false, length = 20)
    private String locale;
} 