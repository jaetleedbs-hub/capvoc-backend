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
 * 메모카테고리관리
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sup_memo_ctgy", schema = "cdm_own")
public class SupMemoCtgyEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 메모카테고리관리 복합키
     */
    @EmbeddedId
    private SupMemoCtgyPkey id;

    /**
     * 메모 카테고리 명
     */
    @Column(name = "memo_ctgy_nm", length = 100)
    private String memoCtgyNm;

    /**
     * 정렬일련번호
     */
    @Column(name = "sort_sn")
    private Integer sortSn;
}