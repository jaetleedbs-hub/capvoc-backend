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
 * 사용자 변경이력
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usr_hist", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptUserChgHstryEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptUserChgHstryPkey id;

    /**
     * 변경 전 정보
     */
    @Column(name = "chg_bfr_info_cntn", length = 500)
    private String chgBfrInfo;

    /**
     * 변경 후 정보
     */
    @Column(name = "chg_aft_info_cntn", length = 500)
    private String chgAftrInfo;

    /**
     * 처리자 ID
     */
    @Column(name = "prcs_usr_id", length = 255)
    private String prcrId;

    /**
     * 처리자 조직 코드
     */
    @Column(name = "prcs_ognz_cd", length = 50)
    private String prcrOgnzCd;

    /**
     * 처리 일시
     */
    @Column(name = "prcs_dttm")
    @Convert(converter = buttle.apptpl.common.model.converter.CompactDateTimeStringConverter.class)
    private String prcsDt;
} 