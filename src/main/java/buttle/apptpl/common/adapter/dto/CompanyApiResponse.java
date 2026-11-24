package buttle.apptpl.common.adapter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 회원사 API 공통 응답 DTO
 * 
 * <p>AICC-API 스펙의 표준 응답 구조입니다.</p>
 * 
 * <pre>
 * {
 *   "statusCode": "0000",
 *   "message": "성공",
 *   "responseData": { ... }
 * }
 * </pre>
 * 
 * @param <T> responseData의 타입
 * @since 2026-01-19
 * @author ID-ABO 개발팀
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CompanyApiResponse<T> {
    
    /**
     * 상태 코드
     * <ul>
     *   <li>"0000": 성공</li>
     *   <li>"0001": 파라미터 오류</li>
     *   <li>"0002": 인증 오류</li>
     *   <li>"0004": 내부 서버 오류</li>
     * </ul>
     */
    @JsonProperty("statusCode")
    private String statusCode;
    
    /**
     * 응답 메시지
     */
    @JsonProperty("message")
    private String message;
    
    /**
     * 실제 응답 데이터
     */
    @JsonProperty("responseData")
    private T responseData;
    
    /**
     * 성공 여부 확인
     * 
     * @return statusCode가 "0000"이면 true
     */
    public boolean isSuccess() {
        return "0000".equals(statusCode);
    }
}
