package buttle.apptpl.api.common.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 파일 업로드 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResDto {

    /**
     * 첨부파일 그룹 ID
     */
    private String atchFileGroupId;

    /**
     * 업로드된 파일 개수
     */
    private Integer uploadCnt;

    /**
     * 저장된 파일 목록
     */
    private List<FileInfoDto> savedFileList;

}
