package buttle.apptpl.common.model.converter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CompactDateTimeStringConverter implements AttributeConverter<String, Timestamp> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");

    @Override
    public Timestamp convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank()) {
            return null;
        }

        String value = attribute.trim();
        LocalDateTime dateTime = value.length() == 8
                ? LocalDate.parse(value, DATE_FORMATTER).atStartOfDay()
                : LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        return Timestamp.valueOf(dateTime);
    }

    @Override
    public String convertToEntityAttribute(Timestamp dbData) {
        return dbData == null ? null : dbData.toLocalDateTime().format(DATE_TIME_FORMATTER);
    }
}
