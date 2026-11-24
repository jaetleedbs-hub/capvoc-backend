package buttle.apptpl.api.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import buttle.apptpl.common.data.BaseDto;

/**
 * 서비스 문의 검색 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquirySearchReqDto extends BaseDto {

    @Schema(description = "검색 시작일 (YYYYMMDD)", type = "string")
    private String fromDt;

    @Schema(description = "검색 종료일 (YYYYMMDD)", type = "string")
    private String toDt;

    @Schema(description = "답변 여부 (전체/대기/완료)", type = "string")
    private String answerYn;

    @Schema(description = "문의 유형 코드", type = "string")
    private String inquiryTypeCd;

    @Schema(description = "회원사명", type = "string")
    private String cmpyNm;

    @Schema(description = "검색어 (문의 내용)", type = "string")
    private String keyword;

    @Schema(description = "페이지 사이즈", type = "string", example = "20")
    private String pageSize;
}
