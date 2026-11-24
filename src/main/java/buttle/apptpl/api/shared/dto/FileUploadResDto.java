package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

import java.util.List;

/**
 * 파일 업로드 응답
 * 업로드된 파일 정보
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FileUploadResDto extends BaseDto {
    
    /**
     * 첨부파일그룹ID
     */
    @Schema(description = "첨부파일그룹ID", type = "string")
    private String atchFileGroupId;
    
    /**
     * 업로드 파일 개수
     */
    @Schema(description = "업로드 파일 개수", type = "integer")
    private Integer uploadCnt;
    
    /**
     * 저장된 파일 목록
     */
    @Schema(description = "저장된 파일 목록", type = "array")
    private List<FileInfo> savedFileList;
    
    /**
     * 파일 정보
     */
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class FileInfo {
        /**
         * 파일ID
         */
        @Schema(description = "파일ID", type = "string")
        private String fileId;
        
        /**
         * 원본 파일명
         */
        @Schema(description = "원본 파일명", type = "string")
        private String originalFileName;
        
        /**
         * 저장된 파일명
         */
        @Schema(description = "저장된 파일명", type = "string")
        private String savedFileName;
        
        /**
         * 파일 크기
         */
        @Schema(description = "파일 크기(bytes)", type = "long")
        private Long fileSize;
        
        /**
         * 파일 타입
         */
        @Schema(description = "파일 타입", type = "string")
        private String contentType;
    }
}
