package buttle.apptpl.common.polling.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "admin 폴링 조회 응답")
public class PollingDto {

    // 폴링 데이터가 있는지 여부
    @Schema(description = "폴링 데이터가 있는지 여부")
    private Boolean isEntryData = false;

    
    // 공지사항 조회 여부
    @Schema(description = "공지사항 조회 여부")
    private Boolean systemNotice;

    // 서비스 답변 조회 여부
    @Schema(description = "서비스 답변 조회 여부")
    private Boolean serviceAnswer;

    // 어드민 알림 제목/내용 정보
    @Schema(description = "어드민 알림 제목/내용 정보")
    private Boolean umsSedngInfo;

}
