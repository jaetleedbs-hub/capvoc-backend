package buttle.apptpl.cmm.ognz.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

/**
 * 조직관리 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0010003Dto extends BaseDto {

    @Schema(description = "조직 코드", type = "String")
    private String ognzCd;

    @Schema(description = "조직 명", type = "String")
    private String ognzNm;

    @Schema(description = "조직 레벨", type = "BigDecimal")
    private BigDecimal ognzLvl;

    @Schema(description = "상위 조직 코드", type = "String")
    private String upOgnzCd;

    @Schema(description = "상위 조직 명", type = "String")
    private String upOgnzCdNm;

    @Schema(description = "조직 경로 명", type = "String")
    private String ognzPathNm;

    @Schema(description = "인사 조직 코드", type = "String")
    private String hrOgnzCd;

    @Schema(description = "조직 대표 전화 번호", type = "String")
    private String ognzRprsTelNo;

    @Schema(description = "정렬 일련번호", type = "BigDecimal")
    private BigDecimal sortSn;

    @Schema(description = "사용 여부", type = "String")
    private String useYn;
}