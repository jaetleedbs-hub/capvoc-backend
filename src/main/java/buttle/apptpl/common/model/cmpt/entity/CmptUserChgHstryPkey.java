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
public class CmptUserChgHstryPkey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "usr_id", nullable = false, length = 255)
    private String userId;

    @Column(name = "chg_dttm", nullable = false)
    @jakarta.persistence.Convert(converter = buttle.apptpl.common.model.converter.CompactDateTimeStringConverter.class)
    private String chgDt;

    @Column(name = "chg_tpcd", nullable = false, length = 20)
    private String chgTypeCd;
}