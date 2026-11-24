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
 * 즐겨찾기카테고리관리
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sup_bokmk_ctgy")
public class SupBokmkCtgyEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 즐겨찾기카테고리관리 복합키
     */
    @EmbeddedId
    private SupBokmkCtgyPkey id;

    /**
     * 즐겨찾기 카테고리 명
     */
    @Column(name = "bokmk_ctgy_nm", length = 100)
    private String bokmkCtgyNm;

    /**
     * 정렬일련번호
     */
    @Column(name = "sort_sn")
    private Integer sortSn;
} 