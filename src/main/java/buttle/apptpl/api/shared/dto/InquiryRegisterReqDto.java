package buttle.apptpl.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 서비스 문의 등록 요청 DTO
 *
 * <p>상담사 콘솔(desk-backend)이 서버 간 호출로 서비스 문의를 접수할 때 사용한다.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "서비스 문의 등록 요청")
public class InquiryRegisterReqDto {

    @Schema(description = "회원사 ID")
    private String companyId;

    @Schema(description = "문의 분류 코드")
    private String srvcQstnClsfCd;

    @Schema(description = "문의 내용")
    private String srvcQstnCn;

    @Schema(description = "등록자 ID")
    private String rgtrId;

    @Schema(description = "수정자 ID")
    private String mdfrId;

    @Schema(description = "문의자명")
    private String svcQstnNm;

    @Schema(description = "첨부파일 그룹 ID")
    private String atchFileGroupId;
}
