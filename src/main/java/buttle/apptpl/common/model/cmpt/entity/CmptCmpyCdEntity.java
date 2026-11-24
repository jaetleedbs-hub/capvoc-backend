package buttle.apptpl.common.model.cmpt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;

import java.io.Serial;
import java.io.Serializable;

/**
 * 회원사유형별코드
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_cmpy_cd")
public class CmptCmpyCdEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptCmpyCdPkey id;

    /**
     * 코드명
     */
    @Column(name = "cd_nm", nullable = true, length = 100)
    private String cdNm;

    /**
     * 코드구분
     */
    @Column(name = "cd_se", nullable = true, length = 20)
    private String cdSe;

    /**
     * 사용여부
     */
    @Column(name = "use_yn", nullable = true, length = 1)
    private String useYn;

    /**
     * 정렬일련번호
     */
    @Column(name = "sort_sn", nullable = true)
    private java.math.BigDecimal sortSn;
}
