package buttle.apptpl.cmm.user.dto;

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
 * 조직목록 응답 DTO
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SYS0040002Dto extends BaseDto {

    @Schema(description = "조직 코드", type = "String")
    private String ognzCd;

    @Schema(description = "조직 명", type = "String")
    private String ognzNm;

    @Schema(description = "조직 레벨", type = "BigDecimal")
    private BigDecimal ognzLvl;

    @Schema(description = "상위 조직 코드", type = "String")
    private String upOgnzCd;

    @Schema(description = "정렬 일련번호", type = "BigDecimal")
    private BigDecimal sortSn;
}