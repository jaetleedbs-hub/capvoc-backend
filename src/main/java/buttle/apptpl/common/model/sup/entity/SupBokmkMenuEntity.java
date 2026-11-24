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
 * 즐겨찾기메뉴관리
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sup_bokmk_menu")
public class SupBokmkMenuEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 즐겨찾기메뉴관리 복합키
     */
    @EmbeddedId
    private SupBokmkMenuPkey id;

    /**
     * 정렬일련번호
     */
    @Column(name = "sort_sn")
    private Integer sortSn;
} 