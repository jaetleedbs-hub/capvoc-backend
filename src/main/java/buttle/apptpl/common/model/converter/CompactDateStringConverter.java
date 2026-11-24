package buttle.apptpl.common.model.converter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CompactDateStringConverter implements AttributeConverter<String, Date> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    @Override
    public Date convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank()) {
            return null;
        }
        return Date.valueOf(LocalDate.parse(attribute.trim(), FORMATTER));
    }

    @Override
    public String convertToEntityAttribute(Date dbData) {
        return dbData == null ? null : dbData.toLocalDate().format(FORMATTER);
    }
}
