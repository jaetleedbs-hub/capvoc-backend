package buttle.apptpl.api.common.components.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.data.BaseDto;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CurrentDatetimeDto extends BaseDto {
    private String currentDatetime;
    private String currentDate;
    private String currentTime;
    private String currentYear;
    private String currentMonth;
    private String currentDay;
    private String currentHour;
    private String currentMinute;
    private Timestamp currentTimestamp;
}
