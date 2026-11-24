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
public class CmptSaleDayPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "sale_day", nullable = false, length = 8)
    private String saleDay;

    @Column(name = "ognz_cd", nullable = false, length = 50)
    private String ognzCd;
} 