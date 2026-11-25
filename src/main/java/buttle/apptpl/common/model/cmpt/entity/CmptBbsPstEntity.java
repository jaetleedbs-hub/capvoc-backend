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
 * 게시물
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_bbs_pst", schema = "cdm_own")
public class CmptBbsPstEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 게시물ID
     */
    @Id
    @Column(name = "pst_id", nullable = false, length = 50)
    private String id;

    /**
     * 게시판ID
     */
    @Column(name = "bbs_id", nullable = false, length = 50)
    private String bbsId;

    /**
     * 게시소속코드
     */
    @Column(name = "bbs_ogdp_cd", length = 20)
    private String bbsOgdpCd;

    /**
     * 게시서비스타입코드
     */
    @Column(name = "bbs_serv_type_cd", length = 20)
    private String bbsServTypeCd;

    /**
     * 게시물분류코드
     */
    @Column(name = "bbs_type_cd", length = 20)
    private String bbsTypeCd;

    /**
     * 제목
     */
    @Column(name = "ttl", length = 256)
    private String ttl;

    /**
     * 게시물내용
     */
    @Column(name = "pst_cn", columnDefinition = "text")
    private String pstCn;

    /**
     * 게시시작일자
     */
    @Column(name = "bbs_st_ymd", length = 8)
    private String bbsStYmd;

    /**
     * 게시시작시간
     */
    @Column(name = "bbs_st_tm", length = 6)
    private String bbsStTm;

    /**
     * 게시종료일자
     */
    @Column(name = "bbs_ed_ymd", length = 8)
    private String bbsEdYmd;

    /**
     * 게시종료시간
     */
    @Column(name = "bbs_ed_tm", length = 6)
    private String bbsEdTm;

    /**
     * 공지여부
     */
    @Column(name = "ntc_yn", length = 1)
    private String ntcYn;

    /**
     * 상단고정여부
     */
    @Column(name = "top_std_yn", length = 1)
    private String topStdYn;

    /**
     * 첨부파일그룹ID
     */
    @Column(name = "atch_file_group_id", length = 50)
    private String atchFileGroupId;

    /**
     * 이미지파일그룹ID
     */
    @Column(name = "img_file_group_id", length = 50)
    private String imgFileGroupId;

    /**
     * 범주코드
     */
    @Column(name = "ctgry_cd", length = 3)
    private String ctgryCd;

    /**
     * 상단고정
     */
    @Column(name = "pstg_yn", length = 1)
    private String pstgYn;

    /**
     * 편집기사용여부
     */
    @Column(name = "edtr_use_yn", length = 1)
    private String edtrUseYn;

    /**
     * 조회수
     */
    @Column(name = "inq_cnt", precision = 10, scale = 0)
    private BigDecimal inqCnt;

    /**
     * 노출사용여부
     */
    @Column(name = "view_use_yn", length = 1)
    private String viewUseYn;

    /**
     * 사용여부
     */
    @Column(name = "use_yn", length = 1)
    private String useYn;

    /**
     * 댓글건수
     */
    @Column(name = "cmnt_nocs", precision = 10, scale = 0)
    private BigDecimal cmntNocs;

    /**
     * PC용썸네일파일ID
     */
    @Column(name = "pc_thumbnail_file_id", length = 50)
    private String pcThumbnailFileId;

    /**
     * 모바일용썸네일파일ID
     */
    @Column(name = "mobile_thumbnail_file_id", length = 50)
    private String mobileThumbnailFileId;
}

