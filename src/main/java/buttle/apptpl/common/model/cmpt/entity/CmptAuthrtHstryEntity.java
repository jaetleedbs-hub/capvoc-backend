package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;

/**
 * 권한이력
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_authrt_hstry", schema = "cdm_own")
public class CmptAuthrtHstryEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 이력ID
     */
    @Id
    @Column(name = "hstry_id", length = 50)
    private String hstryId;

    /**
     * 사용자 ID
     */
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    /**
     * 권한 코드
     */
    @Column(name = "authrt_cd", nullable = false, length = 20)
    private String authrtCd;

    /**
     * 이력일시
     */
    @Column(name = "hstry_dt")
    private OffsetDateTime hstryDt;

    /**
     * 권한 엔티티와의 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authrt_cd", referencedColumnName = "auth_cd", insertable = false, updatable = false)
    private CmptAuthrtEntity cmptAuthrt;
} 