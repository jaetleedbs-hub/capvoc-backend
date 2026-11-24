package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

/**
 * 권한
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptAuthrtEntity extends BaseAuditingEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 권한 코드
     */
    @Id
    @Column(name = "auth_cd", length = 20)
    private String authrtCd;

    /**
     * 권한 명
     */
    @Column(name = "auth_nm", nullable = false, length = 100)
    private String authrtNm;

    /**
     * 권한 구분
     */
    @Column(name = "auth_dvcd", length = 20)
    private String authrtSe;

    /**
     * 조직 조회 범위 코드
     */
    @Column(name = "ognz_inq_rang_cd", length = 20)
    private String ognzInqRangCd;

    /**
     * 기본 화면 메뉴 ID
     */
    @Column(name = "base_scrn_menu_id", length = 50)
    private String bscScrnMenuId;

    /**
     * 정렬일련번호
     */
    @Column(name = "sort_seq")
    private Integer sortSn;

    /**
     * 사용 여부
     */
    @Column(name = "use_yn", nullable = false, length = 1)
    private String useYn;

    @Builder.Default
    @OneToMany(mappedBy = "cmptAuthrt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CmptAuthrtHstryEntity> authrtHstry = new ArrayList<>();
} 