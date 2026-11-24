package buttle.apptpl.api.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 문의 유형 목록 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryTypeListResDto {

    /**
     * 코드
     */
    private String cd;

    /**
     * 코드명 (분류명)
     */
    private String cdNm;

    /**
     * 정렬 순서
     */
    private Integer sortSn;

    /**
     * 사용 여부
     */
    private String useYn;

    /**
     * 수정자 ID
     */
    private String mdfrId;

    /**
     * 수정 일시
     */
    private String mdfcnDt;
}
