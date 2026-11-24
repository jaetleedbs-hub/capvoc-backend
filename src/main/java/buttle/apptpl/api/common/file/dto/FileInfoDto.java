package buttle.apptpl.api.common.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 파일 정보 조회 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoDto {

    /**
     * 파일 ID
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
     * 파일 크기
     */
    private Long fileSize;

    /**
     * 저장된 파일명
     */
    private String savedFileName;

    /**
     * 컨텐트 타입
     */
    private String contentType;

    /**
     * 다운로드 횟수
     */
    private Integer downloadCnt;

    /**
     * 등록일시
     */
    private LocalDateTime regDt;
}
