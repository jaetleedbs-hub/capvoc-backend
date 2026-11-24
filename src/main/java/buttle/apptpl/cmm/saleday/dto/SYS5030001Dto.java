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
@Schema(description = "영업일 요청")
public class SYS5030001Dto extends BaseDto {

    @Schema(description = "시작 영업 일", type = "String")
    private String fromSaleDay;

    @Schema(description = "종료 영업 일", type = "String")
    private String toSaleDay;

    @Schema(description = "조직 코드", type = "String", defaultValue = "DEFAULT_ORG")
    private String ognzCd= "DEFAULT_ORG";

    @Schema(description = "휴일 구분 코드", type = "String")
    private String hldySeCd;

    @Schema(description = "강제 데이터 생성 여부", type = "String")
    private String forceCreateYn;

}