package buttle.apptpl.common.util;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


/**
 * Spring 기반 암호화/복호화 유틸리티 클래스
 * Jasypt를 사용하여 민감한 정보를 암호화/복호화합니다.
 * 
 * 사용 예시:
 * 1. Spring Context 내에서: CryptoUtil.encryptStatic("평문")
 * 2. Spring Context 없이: CryptoUtil.encryptWithoutContext("평문", "키")
 * 3. 복호화: CryptoUtil.decryptStatic("ENC(암호화된문자열)")
 * 4. 설정 파일에서 사용: ENC(암호화된문자열)
 * 
 * 독립 실행이 필요한 경우 EncryptionTool 클래스를 사용하세요.
 * @see buttle.apptpl.common.util.EncryptionTool
 * 
 * @author AICC Development Team
 * @since 2024-01-15
 */
@Slf4j
@Component
public class CryptoUtil {

    @Autowired
    private StringEncryptor stringEncryptor;
    
    // 정적 인스턴스 (유틸리티 메서드를 위한)
    private static CryptoUtil instance;
    
    // 생성자에서 정적 인스턴스 설정
    public CryptoUtil() {
        instance = this;
    }
    
    /**
     * 문자열을 암호화합니다.
     * 
     * @param plainText 암호화할 평문
     * @return 암호화된 문자열 (Base64 인코딩)
     */
    public String encrypt(String plainText) {
        try {
            if (plainText == null || plainText.trim().isEmpty()) {
                throw new IllegalArgumentException("암호화할 텍스트가 비어있습니다.");
            }
            
            String encrypted = stringEncryptor.encrypt(plainText);
            log.debug("텍스트 암호화 완료: {} -> {}", 
                     maskString(plainText), encrypted.substring(0, Math.min(20, encrypted.length())) + "...");
            return encrypted;
            
        } catch (IllegalArgumentException e) {
            // 입력 검증 실패 (null, 빈 문자열 등)
            log.warn("암호화 입력 검증 실패: {}", e.getMessage());
            throw new IllegalArgumentException("암호화할 데이터가 유효하지 않습니다: " + e.getMessage(), e);
            
        } catch (EncryptionOperationNotPossibleException e) {
            // Jasypt 암호화 작업 실패 (키 오류, 알고리즘 문제 등)
            log.error("Jasypt 암호화 작업 실패: {}", e.getMessage());
            throw new RuntimeException("암호화 작업을 수행할 수 없습니다", e);
            
        } catch (SecurityException e) {
            // 보안 정책으로 인한 암호화 실패
            log.error("보안 정책으로 인한 암호화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("보안 정책으로 인해 암호화할 수 없습니다", e);
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("암호화 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("암호화 실패", e);
        }
    }
    
    /**
     * 암호화된 문자열을 복호화합니다.
     * 
     * @param encryptedText 복호화할 암호화된 문자열
     * @return 복호화된 평문
     */
    public String decrypt(String encryptedText) {
        try {
            if (encryptedText == null || encryptedText.trim().isEmpty()) {
                throw new IllegalArgumentException("복호화할 텍스트가 비어있습니다.");
            }
            
            // ENC() 형태인 경우 괄호 안의 내용만 추출
            String textToDecrypt = encryptedText;
            if (encryptedText.startsWith("ENC(") && encryptedText.endsWith(")")) {
                textToDecrypt = encryptedText.substring(4, encryptedText.length() - 1);
            }
            
            String decrypted = stringEncryptor.decrypt(textToDecrypt);
            log.debug("텍스트 복호화 완료: {} -> {}", 
                     textToDecrypt.substring(0, Math.min(20, textToDecrypt.length())) + "...", 
                     maskString(decrypted));
            return decrypted;
            
        } catch (IllegalArgumentException e) {
            // 입력 검증 실패 (null, 빈 문자열, 잘못된 형식 등)
            log.warn("복호화 입력 검증 실패: {}", e.getMessage());
            throw new IllegalArgumentException("복호화할 데이터가 유효하지 않습니다: " + e.getMessage(), e);
            
        } catch (EncryptionOperationNotPossibleException e) {
            // Jasypt 복호화 작업 실패 (잘못된 키, 손상된 데이터 등)
            log.error("Jasypt 복호화 작업 실패: {}", e.getMessage());
            throw new RuntimeException("복호화 작업을 수행할 수 없습니다. 암호화 키나 데이터를 확인하세요", e);
            
        } catch (SecurityException e) {
            // 보안 정책으로 인한 복호화 실패
            log.error("보안 정책으로 인한 복호화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("보안 정책으로 인해 복호화할 수 없습니다", e);
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("복호화 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("복호화 실패", e);
        }
    }
    
    /**
     * 설정 파일용 암호화된 문자열을 생성합니다. (ENC() 형태)
     * 
     * @param plainText 암호화할 평문
     * @return ENC(암호화된문자열) 형태의 문자열
     */
    public String encryptForConfig(String plainText) {
        String encrypted = encrypt(plainText);
        return "ENC(" + encrypted + ")";
    }
    
    /**
     * 문자열이 암호화된 형태인지 확인합니다.
     * 
     * @param text 확인할 문자열
     * @return 암호화된 형태이면 true, 아니면 false
     */
    public boolean isEncrypted(String text) {
        return text != null && text.startsWith("ENC(") && text.endsWith(")");
    }
    
    /**
     * 이미 암호화된 값이면 그대로 반환하고, 평문이면 암호화합니다.
     * 복호화를 시도하여 성공하면 이미 암호화된 것으로 판단합니다.
     * 
     * @param text 암호화할 텍스트 또는 이미 암호화된 텍스트
     * @return 암호화된 문자열
     */
    public String encryptIfNeeded(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        try {
            decrypt(text);
            return text;
        } catch (Exception e) {
            return encrypt(text);
        }
    }
    
    /**
     * 민감한 정보를 마스킹합니다. (로그 출력용)
     * 
     * @param text 마스킹할 문자열
     * @return 마스킹된 문자열
     */
    private String maskString(String text) {
        if (text == null || text.length() <= 3) {
            return "***";
        }
        return text.substring(0, 3) + "***";
    }
    
    // ========== 정적 메서드들 (편의성을 위한) ==========
    
    /**
     * 정적 메서드: 문자열 암호화
     * 
     * @param plainText 암호화할 평문
     * @return 암호화된 문자열
     */
    public static String encryptStatic(String plainText) {
        try {
            if (instance == null) {
                throw new RuntimeException("CryptoUtil이 초기화되지 않았습니다. Spring Context를 확인하세요.");
            }
            return instance.encrypt(plainText);
            
        } catch (IllegalArgumentException e) {
            // 입력 검증 예외는 그대로 전파
            throw e;
            
        } catch (RuntimeException e) {
            // 암호화 관련 런타임 예외는 그대로 전파
            throw e;
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("정적 암호화 메서드에서 예상치 못한 오류: {}", e.getMessage(), e);
            throw new RuntimeException("정적 암호화 실패", e);
        }
    }
    
    /**
     * 정적 메서드: 문자열 복호화
     * 
     * @param encryptedText 복호화할 암호화된 문자열
     * @return 복호화된 평문
     */
    public static String decryptStatic(String encryptedText) {
        try {
            if (instance == null) {
                throw new RuntimeException("CryptoUtil이 초기화되지 않았습니다. Spring Context를 확인하세요.");
            }
            return instance.decrypt(encryptedText);
            
        } catch (IllegalArgumentException e) {
            // 입력 검증 예외는 그대로 전파
            throw e;
            
        } catch (RuntimeException e) {
            // 복호화 관련 런타임 예외는 그대로 전파
            throw e;
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("정적 복호화 메서드에서 예상치 못한 오류: {}", e.getMessage(), e);
            throw new RuntimeException("정적 복호화 실패", e);
        }
    }
    
    /**
     * 정적 메서드: 설정 파일용 암호화
     * 
     * @param plainText 암호화할 평문
     * @return ENC(암호화된문자열) 형태
     */
    public static String encryptForConfigStatic(String plainText) {
        try {
            if (instance == null) {
                throw new RuntimeException("CryptoUtil이 초기화되지 않았습니다. Spring Context를 확인하세요.");
            }
            return instance.encryptForConfig(plainText);
            
        } catch (IllegalArgumentException e) {
            // 입력 검증 예외는 그대로 전파
            throw e;
            
        } catch (RuntimeException e) {
            // 암호화 관련 런타임 예외는 그대로 전파
            throw e;
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("정적 설정 파일 암호화 메서드에서 예상치 못한 오류: {}", e.getMessage(), e);
            throw new RuntimeException("정적 설정 파일 암호화 실패", e);
        }
    }
    
    // ========== Spring Context 없이 사용하는 메서드들 ==========
    
    /**
     * Spring Context 없이 암호화 (EncryptionTool 위임)
     * 
     * @param plainText 암호화할 평문
     * @param password 암호화 키
     * @return 암호화된 문자열
     */
    public static String encryptWithoutContext(String plainText, String password) {
        return EncryptionTool.encrypt(plainText, password);
    }
    
    /**
     * Spring Context 없이 복호화 (EncryptionTool 위임)
     * 
     * @param encryptedText 암호화된 문자열
     * @param password 암호화 키
     * @return 복호화된 평문
     */
    public static String decryptWithoutContext(String encryptedText, String password) {
        return EncryptionTool.decrypt(encryptedText, password);
    }
    
    /**
     * Spring Context 없이 설정 파일용 암호화 (EncryptionTool 위임)
     * 
     * @param plainText 암호화할 평문
     * @param password 암호화 키
     * @return ENC(암호화된문자열) 형태
     */
    public static String encryptForConfigWithoutContext(String plainText, String password) {
        return EncryptionTool.encryptForConfig(plainText, password);
    }
}
