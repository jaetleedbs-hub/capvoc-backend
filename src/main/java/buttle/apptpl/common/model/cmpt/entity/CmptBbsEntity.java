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
import java.math.BigDecimal;

/**
 * 게시판
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_bbs")
public class CmptBbsEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 게시판ID
     */
    @Id
    @Column(name = "bbs_id", nullable = false, length = 50)
    private String id;

    /**
     * 게시판명
     */
    @Column(name = "bbs_nm", length = 256)
    private String bbsNm;

    /**
     * 게시물건수
     */
    @Column(name = "pst_nocs", precision = 10, scale = 0)
    private BigDecimal pstNocs;

    /**
     * 게시판타입
     */
    @Column(name = "bbs_type", length = 20)
    private String bbsType;

    /**
     * 게시판사용처코드
     */
    @Column(name = "bbs_plcus_cd", length = 20)
    private String bbsPlcusCd;

    /**
     * 사용여부
     */
    @Column(name = "use_yn", length = 1)
    private String useYn;

    /**
     * 편집기사용여부
     */
    @Column(name = "edtr_use_yn", length = 1)
    private String edtrUseYn;

    /**
     * 공지사용여부
     */
    @Column(name = "ntc_use_yn", length = 1)
    private String ntcUseYn;

    /**
     * 댓글사용여부
     */
    @Column(name = "cmnt_use_yn", length = 1)
    private String cmntUseYn;

    /**
     * 범주사용여부
     */
    @Column(name = "ctgry_use_yn", length = 1)
    private String ctgryUseYn;

    /**
     * 범주코드그룹
     */
    @Column(name = "ctgry_cd_group", length = 20)
    private String ctgryCdGroup;

    /**
     * 첨부사용여부
     */
    @Column(name = "atch_use_yn", length = 1)
    private String atchUseYn;

    /**
     * 첨부파일제한크기(MB)
     */
    @Column(name = "atch_file_lmt_sz", precision = 10, scale = 0)
    private BigDecimal atchFileLmtSz;

    /**
     * 첨부파일제한확장자
     */
    @Column(name = "atch_file_lmt_extn", length = 200)
    private String atchFileLmtExtn;

    /**
     * 썸네일사용여부
     */
    @Column(name = "thumbnail_use_yn", length = 1)
    private String thumbnailUseYn;

    /**
     * 썸네일필수여부
     */
    @Column(name = "thumbnail_esntl_yn", length = 1)
    private String thumbnailEsntlYn;

    /**
     * 목록화면URL주소
     */
    @Column(name = "list_scrn_url_addr", length = 200)
    private String listScrnUrlAddr;

    /**
     * 상세화면URL주소
     */
    @Column(name = "dtl_scrn_url_addr", length = 200)
    private String dtlScrnUrlAddr;

    /**
     * 수정화면URL주소
     */
    @Column(name = "mdfcn_scrn_url_addr", length = 200)
    private String mdfcnScrnUrlAddr;
}

