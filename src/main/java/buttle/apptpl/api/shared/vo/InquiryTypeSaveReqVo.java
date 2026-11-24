package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 문의 유형 등록 요청 VO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InquiryTypeSaveReqVo {

    /**
     * 생성할 코드
     */
    private String cd;

    /**
     * 코드명
     */
    private String cdNm;

    /**
     * 정렬 순서
     */
    private Integer sortSn;

    /**
     * 등록자 ID
     */
    private String rgtrId;

    /**
     * 수정자 ID
     */
    private String mdfrId;
}
