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
 * 메뉴
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu", schema = "cdm_own")
@AttributeOverrides({
    @AttributeOverride(name = "regDt", column = @Column(name = "sys_reg_dttm", updatable = false)),
    @AttributeOverride(name = "rgtrId", column = @Column(name = "sys_reg_usr_id", updatable = false, length = 255)),
    @AttributeOverride(name = "mdfcnDt", column = @Column(name = "sys_chg_dttm")),
    @AttributeOverride(name = "mdfrId", column = @Column(name = "sys_chg_usr_id", length = 255))
})
public class CmptMenuEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 메뉴 ID
     */
    @Id
    @Column(name = "menu_id", nullable = false, length = 50)
    private String menuId;

    /**
     * 메뉴 명
     */
    @Column(name = "menu_nm", length = 100)
    private String menuNm;

    /**
     * URL 주소
     */
    @Column(name = "url_addr", length = 200)
    private String urlAddr;

    /**
     * 새창 여부
     */
    @Column(name = "npag_yn", length = 1)
    private String npagYn;

    /**
     * 상위 메뉴 ID
     */
    @Column(name = "anct_menu_id", length = 50)
    private String upMenuId;

    /**
     * 정렬 일련번호
     */
    @Column(name = "sort_seq")
    private Integer sortSn;

    /**
     * 화면 크기
     */
    @Column(name = "scrn_size", length = 40)
    private String scrnSize;

    /**
     * 사용 여부
     */
    @Column(name = "menu_use_yn", length = 1)
    private String menuUseYn;
} 