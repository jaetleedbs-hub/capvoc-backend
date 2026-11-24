package buttle.apptpl.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 공통 첨부파일 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonAtchFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 첨부파일ID
     */
    private String atchFileId;

    /**
     * 첨부파일그룹ID
     */
    private String atchFileGroupId;

    /**
     * 파일명
     */
    private String fileNm;

    /**
     * 컨텐트타입
     */
    private String cnType;

    /**
     * 파일크기
     */
    private Long fileSz;

    /**
     * 파일경로명
     */
    private String filePathNm;

    /**
     * 다운로드수
     */
    private Integer dwnldCnt;

    /**
     * 삭제여부
     */
    private String delYn;

    /**
     * 등록자ID
     */
    private String rgtrId;

    /**
     * 등록일시
     */
    private LocalDateTime regDt;

    /**
     * 수정자ID
     */
    private String mdfrId;

    /**
     * 수정일시
     */
    private LocalDateTime mdfcnDt;
}
