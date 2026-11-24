package buttle.apptpl.sup.memo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 슬라이드프레임_메모 등록 응답
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class COM2080002Dto extends BaseDto {

    @Schema(description = "사용자 ID", type = "string")
    private String userId;

    @Schema(description = "메모 카테고리 시퀀스ID", type = "integer")
    private Integer memoCtgySid;

    @Schema(description = "메모 시퀀스ID", type = "integer")
    private Integer memoSid;

    @Schema(description = "메모 제목 명", type = "string")
    private String memoTtlNm;

    @Schema(description = "메모 내용", type = "string")
    private String memoCn;

    @Schema(description = "메모 알람 여부", type = "string")
    private String memoAlrmYn;

    @Schema(description = "메모 알람 일시", type = "string")
    private String memoAlrmDt;

    @Schema(description = "정렬일련번호", type = "integer")
    private Integer sortSn;

    @Schema(description = "등록일시", type = "string")
    private String rgstDttm;

    @Schema(description = "수정일시", type = "string")
    private String updtDttm;
} 