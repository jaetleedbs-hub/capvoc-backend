package buttle.apptpl.common.model.sup.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import buttle.apptpl.common.data.BaseAuditingEntity;

/**
 * 메모관리
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sup_memo")
public class SupMemoEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 메모관리 복합키
     */
    @EmbeddedId
    private SupMemoPkey id;

    /**
     * 메모 제목 명
     */
    @Column(name = "memo_ttl_nm", length = 100)
    private String memoTtlNm;

    /**
     * 메모 내용
     */
    @Column(name = "memo_cn", columnDefinition = "TEXT")
    private String memoCn;

    /**
     * 메모 알람 여부
     */
    @Column(name = "memo_alrm_yn", length = 1)
    private String memoAlrmYn;

    /**
     * 메모 알람 일시
     */
    @Column(name = "memo_alrm_dt", length = 14)
    private String memoAlrmDt;   

    /**
     * 정렬일련번호
     */
    @Column(name = "sort_sn")
    private Integer sortSn;
} 