package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;
import buttle.apptpl.common.data.BaseDto;

/**
 * 파일 업로드 요청
 * 사업자등록증 파일 업로드
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FileUploadReqDto extends BaseDto {
    
    /**
     * 회원사ID
     */
    @Schema(description = "회원사ID", type = "string")
    private String cmpyId;
    
    /**
     * 업로드 파일 목록
     */
    @Schema(description = "업로드 파일 목록", type = "array")
    private MultipartFile[] files;
    
    /**
     * 트랜잭션 데이터 (JSON 문자열)
     */
    @Schema(description = "트랜잭션 데이터", type = "string")
    private String transdata;
}
