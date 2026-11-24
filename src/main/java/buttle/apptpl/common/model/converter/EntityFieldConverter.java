package buttle.apptpl.common.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import buttle.apptpl.common.util.EncryptionTool;

/**
 * Entity 필드 암호화/복호화 Converter
 * 
 * 사용법:
 * <pre>
 * {@code
 * @Column(name = "cust_actno", length = 20)
 * @Convert(converter = EntityFieldConverter.class)
 * private String custActno;
 * }
 * </pre>
 */
@Slf4j
@Component
@Converter(autoApply = false)
public class EntityFieldConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return attribute;
        }
        try {
            String encrypted = EncryptionTool.encryptField(attribute);
            return encrypted;
        } catch (IllegalArgumentException e) {
            log.warn("암호화할 값이 유효하지 않음: {}", e.getMessage());
          return attribute;
        } catch (SecurityException e) {
            log.error("보안 정책으로 인한 암호화 실패: {}", e.getMessage(), e);
            return attribute;
        } catch (RuntimeException e) {
            log.error("암호화 처리 실패: {}", e.getMessage(), e);
            return attribute;
        } catch (Exception e) {
            log.error("예상치 못한 암호화 오류: {}", e.getMessage(), e);
            return attribute;
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return dbData;
        }
        try {
            String decrypted = EncryptionTool.decryptField(dbData);
            return decrypted;
        } catch (IllegalArgumentException e) {
            log.error("복호화할 값이 유효하지 않음: {}", e.getMessage(), e);
            return dbData;
        } catch (SecurityException e) {
            log.error("보안 정책으로 인한 복호화 실패: {}", e.getMessage(), e);
            return dbData;
        } catch (RuntimeException e) {
            log.error("복호화 처리 실패: {}", e.getMessage(), e);
            return dbData;
        } catch (Exception e) {
            log.error("예상치 못한 복호화 오류: {}", e.getMessage(), e);
            return dbData;
        }
    }
}

