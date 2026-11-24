package buttle.apptpl.api.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 문의 유형 등록/수정 요청 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryTypeSaveReqDto {

    /**
     * 코드 (수정 시 필요)
     */
    private String cd;

    /**
     * 코드명 (분류명, 최대 100자)
     */
    @NotBlank(message = "분류명을 입력해 주세요.")
    @Size(max = 100, message = "분류명은 100자 이내로 입력해 주세요.")
    private String cdNm;
}
