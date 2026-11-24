package buttle.apptpl.common.model.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class CompactDateStringConverterTest {

    private final CompactDateStringConverter dateConverter = new CompactDateStringConverter();
    private final CompactDateTimeStringConverter dateTimeConverter = new CompactDateTimeStringConverter();

    @Test
    void convertsCompactDateStringsToSqlDates() {
        Date sqlDate = Date.valueOf(LocalDate.of(2026, 11, 21));

        assertEquals(sqlDate, dateConverter.convertToDatabaseColumn("20261121"));
        assertEquals("20261121", dateConverter.convertToEntityAttribute(sqlDate));
        assertNull(dateConverter.convertToDatabaseColumn(""));
        assertThrows(DateTimeParseException.class, () -> dateConverter.convertToDatabaseColumn("20260230"));
    }

    @Test
    void convertsCompactDateTimeStringsAndLegacyDateOnlyValues() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 11, 21, 14, 30, 45);
        Timestamp sqlTimestamp = Timestamp.valueOf(dateTime);

        assertEquals(sqlTimestamp, dateTimeConverter.convertToDatabaseColumn("20261121143045"));
        assertEquals("20261121143045", dateTimeConverter.convertToEntityAttribute(sqlTimestamp));
        assertEquals(Timestamp.valueOf(LocalDate.of(2026, 11, 21).atStartOfDay()),
                dateTimeConverter.convertToDatabaseColumn("20261121"));
        assertNull(dateTimeConverter.convertToDatabaseColumn(" "));
        assertThrows(DateTimeParseException.class, () -> dateTimeConverter.convertToDatabaseColumn("202611"));
    }
}
