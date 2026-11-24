package buttle.apptpl.common.model.cmpt.entity;

import jakarta.persistence.*;
import lombok.*;
import buttle.apptpl.common.data.BaseAuditingEntity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 프로그램
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prgrm", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptPrgrmEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 프로그램 ID
     */
    @Id
    @Column(name = "prgrm_id", nullable = false, length = 50)
    private String prgrmId;

    /**
     * 프로그램 명
     */
    @Column(name = "prgrm_nm", length = 500)
    private String prgrmNm;

    /**
     * http 메소드
     */
    @Column(name = "http_mthd_nm", length = 10)
    private String httpMethod;

    /**
     * URL 주소 패턴
     */
    @Column(name = "url_addr_ptrn_cntn", length = 200)
    private String urlAddrPtrn;

    /**
     * 정렬 일련번호
     */
    @Column(name = "sort_seq")
    private Integer sortSn;

    @Builder.Default
    @OneToMany(mappedBy = "cmptPrgrm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CmptPrgrmAuthrtEntity> prgrmAuthrt = new ArrayList<>();
}