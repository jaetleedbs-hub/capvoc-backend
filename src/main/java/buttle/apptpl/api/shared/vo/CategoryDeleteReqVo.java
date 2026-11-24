package buttle.apptpl.api.shared.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카테고리 삭제 요청 VO
 *
 * @since 2026-01-19
 * @author ID-ABO 개발팀
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDeleteReqVo {
    
    /** 카테고리코드 */
    private String knwlCtgyCd;
    
    /** 상세카테고리코드 */
    private String knwlCtgyDtlCd;

    private String cd;

    /** 회원사ID */
    private String cmpyId;
}
