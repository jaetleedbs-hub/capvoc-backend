package buttle.apptpl.api.common.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ZIP 다운로드 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadZipReqDto {

    /**
     * 파일 ID 목록
     */
    private List<String> fileIds;

    /**
     * ZIP 파일명 (기본값: files.zip)
     */
    private String zipFileName;
}
