package buttle.apptpl.cmm.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 권한등급별 메뉴 조회 응답 DTO
 *
 * <p>필드명은 상담사 콘솔(desk MenuInfoVo)이 그대로 사용하는 계약이므로 임의 개명 금지.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "권한등급별 메뉴")
public class MenuByAuthLevelResDto {

    @Schema(description = "회원사 유형 코드")
    private String cmpyTypeCd;

    @Schema(description = "메뉴 ID")
    private String menuId;

    @Schema(description = "메뉴명")
    private String menuNm;

    @Schema(description = "URL 주소")
    private String tmpltUrlAddr;

    @Schema(description = "새창 여부")
    private String npagYn;

    @Schema(description = "상위 메뉴 ID")
    private String upMenuId;

    @Schema(description = "정렬 일련번호")
    private Integer sortSn;

    @Schema(description = "메뉴 사용 여부")
    private String menuUseYn;

    @Schema(description = "화면 크기")
    private String scrnSize;

    @Schema(description = "권한 코드")
    private String authrtCd;

    @Schema(description = "권한명")
    private String authrtNm;

    @Schema(description = "권한 구분")
    private String authrtSe;

    @Schema(description = "메뉴 레벨 (최상위 1)")
    private Integer menuLvl;
}
