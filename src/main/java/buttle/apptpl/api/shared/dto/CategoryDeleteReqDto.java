package buttle.apptpl.api.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카테고리 삭제 요청 DTO
 *
 * @since 2026-01-19
 * @author ID-ABO 개발팀
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDeleteReqDto {
    
    /** 카테고리코드 */
    private String knwlCtgyCd;
    
    /** 상세카테고리코드 (상세만) */
    private String knwlCtgyDtlCd;
    
    /** 코드 (BO302P1용) */
    private String cd;

    /** 회원사ID (필수) */
    private String cmpyId;
}
