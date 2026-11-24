package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 내 서비스 문의 목록 검색 요청 DTO
 *
 * <p>상담사 콘솔(desk-backend)이 로그인 사용자 본인이 등록한 문의만 조회할 때 사용한다.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "내 서비스 문의 목록 검색 요청")
public class MyInquirySearchReqDto {

    @Schema(description = "회원사 ID")
    private String companyId;

    @Schema(description = "답변 여부 (Y: 답변완료, N: 답변대기, 빈값: 전체)")
    private String answerYn;

    @Schema(description = "조회 대상 사용자 ID (문의 등록자)")
    private String loginId;
}
