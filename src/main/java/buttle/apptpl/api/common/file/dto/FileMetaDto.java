package buttle.apptpl.api.common.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 파일 메타정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetaDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 파일 ID (형식: {시스템코드}-{회원사ID}-{UUID})
     */
    private String fileId;

    /**
     * 파일 그룹 ID
     */
    private String fileGroupId;

    /**
     * 원본 파일명
     */
    private String fileName;

    /**
     * 파일 크기 (bytes)
     */
    private Long fileSize;

    /**
     * 컨텐츠 타입
     */
    private String contentType;

    /**
     * 파일 경로 (상대 경로, 시스템 폴더 포함)
     */
    private String filePath;

    /**
     * 다운로드 횟수
     */
    private Integer downloadCnt;

    /**
     * 등록 일시
     */
    private LocalDateTime regDt;

    /**
     * 시스템 코드 (AICC, ADMIN)
     */
    private String systemCode;

    /**
     * 회원사 ID
     */
    private String companyId;
}
