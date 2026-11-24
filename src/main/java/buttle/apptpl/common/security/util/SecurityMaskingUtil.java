package buttle.apptpl.common.security.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * 보안 마스킹 유틸리티 클래스
 * 
 * Base64 인코딩과 문자 치환을 결합한 난독화 및 복호화 기능을 제공합니다.
 * Java(서버)와 JavaScript(클라이언트) 간에 동일한 문자셋 키를 사용하여
 * 데이터를 안전하게 주고받을 수 있도록 설계되었습니다.
 * 
 * @see ComLib#encryptObfuscated(String) JavaScript의 암호화 메서드
 * @see ComLib#decryptObfuscated(String) JavaScript의 복호화 메서드
 * 
 * @author 개발팀
 * @since 2024-01-01
 */
@Slf4j
public class SecurityMaskingUtil {
    
    private static final String OBFUSCATED_PREFIX = "OBF_";
    
    // 기본 문자셋: 0~9, a~z, A~Z. Base64 인코딩 결과 문자의 일부.
    private static final String BASE_CHARSET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // 치환된 문자셋 (키 역할). JavaScript의 OBFUSCATED_CHARSET과 반드시 동일해야 함.
    private static final String OBFUSCATED_CHARSET = "ZhsIo2yW1GxCXS83airEtubNcdPD9qzYMAlBjO6Vf7QUR4LKvpmHgnJkT5w0Fe";

    // 치환 맵 (인코딩용)
    private static final Map<Character, Character> ENCODE_MAP = new HashMap<>();
    // 치환 맵 (디코딩용)
    private static final Map<Character, Character> DECODE_MAP = new HashMap<>();
    
    static {
        // 정적 초기화 블록: 클래스 로딩 시 한 번만 실행
        // 인코딩/디코딩 맵 초기화
        for (int i = 0; i < BASE_CHARSET.length(); i++) {
            char baseChar = BASE_CHARSET.charAt(i);
            char obfuscatedChar = OBFUSCATED_CHARSET.charAt(i);
            ENCODE_MAP.put(baseChar, obfuscatedChar);
            DECODE_MAP.put(obfuscatedChar, baseChar);
        }
    }

    /**
     * 간단 암호화 (내부적으로 난독화 암호화 호출)
     * @param plainText 평문
     * @return 난독화된 문자열
     */
    public static String encryptSimple(String plainText) {
        return SecurityMaskingUtil.encryptObfuscated(plainText);
    }
    
    /**
     * 간단 복호화 (내부적으로 난독화 복호화 호출)
     * @param source 암호화된 문자열
     * @return 복호화된 문자열
     */
    public static String decryptSimple(String source) {
        return SecurityMaskingUtil.decryptObfuscated(source);
    }
    
    /**
     * 마스킹 여부 확인
     * @param text 확인할 텍스트
     * @return 난독화 여부
     */
    public static boolean isSimpleEncoded(String text) {
        return SecurityMaskingUtil.isObfuscated(text);
    }
    
    /**
     * 고급 난독화 암호화 (Base64 + 문자 치환)
     * 1. UTF-8 바이트 변환
     * 2. Base64 인코딩
     * 3. 문자 치환
     * 
     * @param plainText 평문
     * @return 'OBF_' 프리픽스가 붙은 난독화된 문자열
     */
    public static String encryptObfuscated(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            log.debug("🔍 Java 암호화 단계별 로그:");
            log.debug("1. 원본 텍스트: " + plainText);
            
            // 1. UTF-8 바이트 배열로 변환
            byte[] utf8Bytes = plainText.getBytes(StandardCharsets.UTF_8);
            log.debug("2. UTF-8 바이트 배열: " + java.util.Arrays.toString(utf8Bytes));
            
            // 2. Base64 인코딩
            String base64Encoded = Base64.getEncoder().encodeToString(utf8Bytes);
            log.debug("3. Base64 인코딩: " + base64Encoded);
            
            // 3. 문자 치환을 통한 난독화 ('=' 패딩 문자는 제외)
            StringBuilder obfuscated = new StringBuilder();
            for (char c : base64Encoded.toCharArray()) {
                if (c == '=') {
                    obfuscated.append(c); // 패딩 문자는 그대로 유지
                } else {
                    obfuscated.append(ENCODE_MAP.getOrDefault(c, c));
                }
            }
            
            log.debug("4. 문자 치환 완료: " + obfuscated.toString());
            String result = OBFUSCATED_PREFIX + obfuscated.toString();
            log.debug("5. 최종 결과: " + result);
            
            return result;
        } catch (OutOfMemoryError e) {
            log.error("암호화 중 메모리 부족 오류 발생 - 입력 데이터 크기: {} bytes", plainText.length());
            return plainText;
        } catch (IllegalArgumentException e) {
            log.error("암호화 중 잘못된 인자 오류 발생: {}", e.getMessage());
            return plainText;
        } catch (Exception e) {
            log.error("암호화 중 예상치 못한 오류 발생: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return plainText;
        }
    }
    
    /**
     * 고급 난독화 복호화
     * 1. 문자 치환 복원
     * 2. Base64 디코딩
     * 3. UTF-8 문자열 변환
     * 
     * @param obfuscatedText 난독화된 문자열
     * @return 복호화된 문자열
     */
    public static String decryptObfuscated(String obfuscatedText) {
        if (obfuscatedText == null || obfuscatedText.isEmpty()) {
            return obfuscatedText;
        }
        
        String encodedText = obfuscatedText;
        
        log.debug("🔍 Java 복호화 단계별 로그:");
        log.debug("1. 입력값: " + obfuscatedText);
        
        // 프리픽스 제거
        if (encodedText.startsWith(OBFUSCATED_PREFIX)) {
            encodedText = encodedText.substring(OBFUSCATED_PREFIX.length());
        }
        log.debug("2. 프리픽스 제거: " + encodedText);
        
        try {
            // 1. 문자 치환 복원 ('=' 패딩 문자는 제외)
            StringBuilder restored = new StringBuilder();
            for (char c : encodedText.toCharArray()) {
                if (c == '=') {
                    restored.append(c); // 패딩 문자는 그대로 유지
                } else {
                    restored.append(DECODE_MAP.getOrDefault(c, c));
                }
            }
            log.debug("3. 문자 치환 복원: " + restored.toString());
            
            // 2. Base64 디코딩
            byte[] decodedBytes = Base64.getDecoder().decode(restored.toString());
            log.debug("4. Base64 디코딩 완료, 바이트 배열: " + java.util.Arrays.toString(decodedBytes));
            
            String result = new String(decodedBytes, StandardCharsets.UTF_8);
            log.debug("5. UTF-8 문자열 변환: " + result);
            
            return result;
        } catch (IllegalArgumentException e) {
            log.error("복호화 중 Base64 디코딩 오류 발생 - 잘못된 형식의 데이터: {}", e.getMessage());
            return obfuscatedText;
        } catch (OutOfMemoryError e) {
            log.error("복호화 중 메모리 부족 오류 발생 - 입력 데이터 크기: {} bytes", obfuscatedText.length());
            return obfuscatedText;
        } catch (StringIndexOutOfBoundsException e) {
            log.error("복호화 중 문자열 인덱스 오류 발생 - 데이터 길이: {}", obfuscatedText.length());
            return obfuscatedText;
        } catch (Exception e) {
            log.error("복호화 중 예상치 못한 오류 발생: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return obfuscatedText;
        }
    }
    
    /**
     * 난독화 여부 확인
     * @param text 확인할 텍스트
     * @return 'OBF_' 프리픽스 존재 여부
     */
    public static boolean isObfuscated(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        return text.startsWith(OBFUSCATED_PREFIX);
    }
    
    /**
     * 문자셋 키 정보 반환 (JavaScript 등 다른 시스템과 키 공유 시 사용 가능)
     * @return 키 정보 맵
     */
    public static Map<String, String> getObfuscationKeys() {
        Map<String, String> keys = new HashMap<>();
        keys.put("baseCharset", BASE_CHARSET);
        keys.put("obfuscatedCharset", OBFUSCATED_CHARSET);
        keys.put("prefix", OBFUSCATED_PREFIX);
        return keys;
    }
    
} 