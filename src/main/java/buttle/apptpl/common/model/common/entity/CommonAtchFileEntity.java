package buttle.apptpl.common.model.common.entity;

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
 * 공통 첨부파일
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_atch_file", schema = "cdm_own")
public class CommonAtchFileEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 첨부파일ID
     */
    @Id
    @Column(name = "atch_file_id", nullable = false, length = 50)
    private String id;

    /**
     * 첨부파일그룹ID
     */
    @Column(name = "atch_file_group_id", nullable = false, length = 50)
    private String atchFileGroupId;

    /**
     * 파일명
     */
    @Column(name = "file_nm", length = 300)
    private String fileNm;

    /**
     * 컨텐트타입
     */
    @Column(name = "cn_type", length = 100)
    private String cnType;

    /**
     * 파일크기
     */
    @Column(name = "file_sz", precision = 14, scale = 0)
    private BigDecimal fileSz;

    /**
     * 파일경로명
     */
    @Column(name = "file_path_nm", length = 300)
    private String filePathNm;

    /**
     * 다운로드수
     */
    @Column(name = "dwnld_cnt", precision = 10, scale = 0)
    private BigDecimal dwnldCnt;

    /**
     * 삭제여부
     */
    @Column(name = "del_yn", length = 1)
    private String delYn;
}
