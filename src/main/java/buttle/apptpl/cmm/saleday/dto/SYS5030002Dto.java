package buttle.apptpl.cmm.saleday.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Schema(description = "영업일 응답")
public class SYS5030002Dto extends BaseDto {

    @Schema(description = "영업 일", type = "String")
    private String saleDay;

    @Schema(description = "조직 코드", type = "String")
    private String ognzCd;

    @Schema(description = "조직 코드 명", type = "String")
    private String ognzCdNm;

    @Schema(description = "휴일 구분 코드", type = "String")
    private String hldySeCd;

    @Schema(description = "휴일 구분 코드 명", type = "String")
    private String hldySeCdNm;

    @Schema(description = "요일 구분 코드", type = "String")
    private String wdaySeCd;

    @Schema(description = "요일 구분 코드 명", type = "String")
    private String wdaySeCdNm;

    @Schema(description = "비고", type = "String")
    private String rmrk;

}