package buttle.apptpl.common.adapter.exception;

import lombok.Getter;

/**
 * 회원사 API 호출 예외
 * 
 * <p>회원사 외부 API 호출 시 발생하는 예외를 처리합니다.</p>
 * 
 * @since 2026-01-15
 * @author ID-ABO 개발팀
 */
@Getter
public class CompanyApiException extends RuntimeException {
    
    /** 회원사ID */
    private final String companyId;
    
    /** API 엔드포인트 */
    private final String endpoint;
    
    /** HTTP 상태 코드 */
    private final int statusCode;
    
    /** 응답 본문 */
    private final String responseBody;
    
    /**
     * 생성자
     * 
     * @param companyId 회원사ID
     * @param endpoint API 엔드포인트
     * @param statusCode HTTP 상태 코드
     * @param responseBody 응답 본문
     * @param cause 원인 예외
     */
    public CompanyApiException(String companyId, String endpoint, int statusCode, String responseBody, Throwable cause) {
        // responseBody 에는 desk 가 내려준 실제 사유(예: "회원명을 입력하세요", "매니저 계정수 상한 초과")가 담긴다.
        // 메시지에 함께 실어야 상위 어댑터의 getMessage() 를 통해 사용자에게 진짜 원인이 전달된다
        // (빠뜨리면 화면엔 statusCode 만 보여 무엇을 고쳐야 할지 알 수 없다).
        super(String.format("회원사 API 호출 실패 - companyId: %s, endpoint: %s, statusCode: %d%s",
                companyId, endpoint, statusCode,
                (responseBody != null && !responseBody.isBlank()) ? " - " + responseBody : ""), cause);
        this.companyId = companyId;
        this.endpoint = endpoint;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
    
    /**
     * 생성자 (원인 예외 없음)
     * 
     * @param companyId 회원사ID
     * @param endpoint API 엔드포인트
     * @param statusCode HTTP 상태 코드
     * @param responseBody 응답 본문
     */
    public CompanyApiException(String companyId, String endpoint, int statusCode, String responseBody) {
        this(companyId, endpoint, statusCode, responseBody, null);
    }
}
