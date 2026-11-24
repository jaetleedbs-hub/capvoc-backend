package buttle.apptpl.api.common.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 업로드 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadReqDto {

    /**
     * 업로드할 파일 배열
     */
    private MultipartFile[] files;

    /**
     * 트랜잭션 데이터 (프론트엔드 호환용, 옵션)
     */
    private String transdata;
}
