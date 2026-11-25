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
 * 회원사유형별메뉴
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_cmpy_menu", schema = "cdm_own")
public class CmptCmpyMenuEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptCmpyMenuPkey id;

    /**
     * 메뉴 명
     */
    @Column(name = "menu_nm", nullable = true, length = 100)
    private String menuNm;

    /**
     * URL 주소
     */
    @Column(name = "url_addr", nullable = true, length = 200)
    private String urlAddr;

    /**
     * 새창 여부
     */
    @Column(name = "npag_yn", nullable = true, length = 1)
    private String npagYn;

    /**
     * 상위 메뉴 ID
     */
    @Column(name = "up_menu_id", nullable = true, length = 50)
    private String upMenuId;

    /**
     * 정렬 일련번호
     */
    @Column(name = "sort_sn", nullable = true)
    private Integer sortSn;

    /**
     * 팝업화면 크기
     */
    @Column(name = "scrn_size", nullable = true, length = 20)
    private String scrnSize;

    /**
     * 메뉴 사용 여부
     */
    @Column(name = "menu_use_yn", nullable = true, length = 1)
    private String menuUseYn;
}
