package buttle.apptpl.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 공통 첨부파일 그룹 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonAtchFileGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 첨부파일그룹ID
     */
    private String atchFileGroupId;

    /**
     * 첨부파일 목록
     */
    private List<CommonAtchFile> files;

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
