package buttle.apptpl.api.common.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 파일 메타정보 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetaResDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 결과 코드 (0: 성공, -1: 실패)
     */
    private String result;

    /**
     * 메시지
     */
    private String message;

    /**
     * 단일 파일 데이터
     */
    private FileMetaDto data;

    /**
     * 파일 목록 데이터
     */
    private List<FileMetaDto> dataList;

    /**
     * 성공 응답 생성 (단일 파일)
     */
    public static FileMetaResDto success(FileMetaDto data) {
        return FileMetaResDto.builder()
                .result("0")
                .data(data)
                .build();
    }

    /**
     * 성공 응답 생성 (파일 목록)
     */
    public static FileMetaResDto successList(List<FileMetaDto> dataList) {
        return FileMetaResDto.builder()
                .result("0")
                .dataList(dataList)
                .build();
    }

    /**
     * 에러 응답 생성
     */
    public static FileMetaResDto error(String message) {
        return FileMetaResDto.builder()
                .result("-1")
                .message(message)
                .build();
    }
}
