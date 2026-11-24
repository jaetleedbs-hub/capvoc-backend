package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;

/**
 * 조직
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ognz", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptOgnzEntity extends BaseAuditingEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 조직 코드
     */
    @Id
    @Column(name = "ognz_cd", length = 50)
    private String ognzCd;

    /**
     * 조직 명
     */
    @Column(name = "ognz_nm", length = 100)
    private String ognzNm;

    /**
     * 조직 레벨
     */
    @Column(name = "ognz_lvl")
    private Integer ognzLvl;

    /**
     * 조직 경로 명
     */
    @Column(name = "ognz_path_nm", length = 255)
    private String ognzPathNm;

    /**
     * 인사 조직 코드
     */
    @Column(name = "hr_ognz_cd", length = 50)
    private String hrOgnzCd;

    /**
     * 조직 대표 전화 번호
     */
    @Column(name = "ognz_rprs_tel_no", length = 11)
    private String ognzRprsTelNo;

    /**
     * 정렬 일련번호
     */
    @Column(name = "sort_seq")
    private Integer sortSn;

    /**
     * 사용 여부
     */
    @Column(name = "use_yn", length = 1)
    private String useYn;

    /**
     * 하위 조직 목록
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CmptOgnzEntity> children = new ArrayList<>();

    /**
     * 상위 조직
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anct_ognz_cd")
    private CmptOgnzEntity parent;

    @Builder.Default
    @OneToMany(mappedBy = "cmptOgnz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CmptUserEntity> users = new ArrayList<>();
} 