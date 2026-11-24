package buttle.apptpl.common.adapter.dto;

import lombok.*;

/**
 * 회원사 API 정보 DTO
 * 
 * <p>회원사별로 등록된 외부 API 서버 정보를 담는 DTO입니다.</p>
 * 
 * @since 2026-01-15
 * @author ID-ABO 개발팀
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CompanyApiInfo {
    
    /** 회원사ID */
    private String companyId;
    
    /** 회원사명 */
    private String companyName;
    
    /** API 서버 URL (예: https://api.company.com) */
    private String apiServerUrl;
    
    /** API 인증 토큰 (Bearer 토큰) */
    private String apiAuthToken;
    
    /** API Key (선택 사항) */
    private String apiKey;
    
    /** 타임아웃 (초) - null인 경우 기본값 사용 */
    private Integer timeoutSeconds;
    
    /** 사용여부 (Y/N) */
    private String useYn;
    
    /** 비고 */
    private String remark;
}
