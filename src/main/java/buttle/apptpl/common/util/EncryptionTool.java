package buttle.apptpl.common.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.salt.StringFixedSaltGenerator;

import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.security.SvcEnvConfig;

/**
 * 독립적인 암호화 도구 클래스 (명령행 실행 특화)
 * Spring Context 없이 사용 가능하며, 명령행에서 실행할 수 있습니다.
 * 개발자가 설정 파일용 암호화된 값을 미리 생성할 때 주로 사용됩니다.
 * 
 * 명령행 사용법:
 * java buttle.apptpl.common.util.EncryptionTool encrypt "평문" [암호키]
 * java buttle.apptpl.common.util.EncryptionTool decrypt "암호화된문자열" [암호키]
 * java buttle.apptpl.common.util.EncryptionTool config "평문" [암호키]
 * java buttle.apptpl.common.util.EncryptionTool test "테스트문자열" [암호키]
 * java buttle.apptpl.common.util.EncryptionTool encrypt-deterministic "평문" [암호키]  (해시를 사용하지 않는 deterministic 암호화)
 * java buttle.apptpl.common.util.EncryptionTool decrypt-deterministic "암호화된문자열" [암호키]
 * java buttle.apptpl.common.util.EncryptionTool config-deterministic "평문" [암호키]
 * java buttle.apptpl.common.util.EncryptionTool test-deterministic "테스트문자열" [암호키]
 * 
 * Spring 애플리케이션 내에서는 CryptoUtil 클래스를 사용하세요.
 * @see buttle.apptpl.common.util.CryptoUtil
 * 
 * @author AICC Development Team
 * @since 2024-01-15
 */
@Slf4j
public class EncryptionTool {
    
    /**
     * 문자열을 암호화합니다.
     * 
     * @param plainText 암호화할 평문
     * @param password 암호화 키
     * @return 암호화된 문자열
     */
    public static String encrypt(String plainText, String password) {
        try {
            if (plainText == null || plainText.trim().isEmpty()) {
                throw new IllegalArgumentException("암호화할 텍스트가 비어있습니다.");
            }
            
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("암호화 키가 비어있습니다.");
            }
            
            PooledPBEStringEncryptor encryptor = createEncryptor(password);
            return encryptor.encrypt(plainText);
            
        } catch (IllegalArgumentException e) {
            // 입력 검증 실패는 그대로 전파
            throw e;
            
        } catch (EncryptionOperationNotPossibleException e) {
            // Jasypt 암호화 작업 실패 (키 오류, 알고리즘 문제 등)
            log.error("Jasypt 암호화 작업 실패: {}", e.getMessage());
            throw new RuntimeException("암호화 작업을 수행할 수 없습니다: " + e.getMessage(), e);
            
        } catch (SecurityException e) {
            // 보안 정책으로 인한 암호화 실패
            log.error("보안 정책으로 인한 암호화 실패: {}", e.getMessage());
            throw new RuntimeException("보안 정책으로 인해 암호화할 수 없습니다", e);
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("암호화 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("암호화 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 암호화된 문자열을 복호화합니다.
     * 
     * @param encryptedText 암호화된 문자열
     * @param password 암호화 키
     * @return 복호화된 평문
     */
    public static String decrypt(String encryptedText, String password) {
        try {
            if (encryptedText == null || encryptedText.trim().isEmpty()) {
                throw new IllegalArgumentException("복호화할 텍스트가 비어있습니다.");
            }
            
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("암호화 키가 비어있습니다.");
            }
            
            // ENC() 형태인 경우 괄호 안의 내용만 추출
            String textToDecrypt = encryptedText;
            if (encryptedText.startsWith("ENC(") && encryptedText.endsWith(")")) {
                textToDecrypt = encryptedText.substring(4, encryptedText.length() - 1);
            }
            
            PooledPBEStringEncryptor encryptor = createEncryptor(password);
            return encryptor.decrypt(textToDecrypt);
            
        } catch (IllegalArgumentException e) {
            // 입력 검증 실패는 그대로 전파
            throw e;
            
        } catch (EncryptionOperationNotPossibleException e) {
            // Jasypt 복호화 작업 실패 (잘못된 키, 손상된 데이터 등)
            log.error("Jasypt 복호화 작업 실패: {}", e.getMessage());
            throw new RuntimeException("복호화 작업을 수행할 수 없습니다. 암호화 키나 데이터를 확인하세요: " + e.getMessage(), e);
            
        } catch (SecurityException e) {
            // 보안 정책으로 인한 복호화 실패
            log.error("보안 정책으로 인한 복호화 실패: {}", e.getMessage());
            throw new RuntimeException("보안 정책으로 인해 복호화할 수 없습니다", e);
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("복호화 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("복호화 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * Jasypt 암호화기를 생성합니다.
     * 
     * @param password 암호화 키
     * @return PooledPBEStringEncryptor 인스턴스
     */
    private static PooledPBEStringEncryptor createEncryptor(String password) {
        try {
            PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            SimpleStringPBEConfig config = new SimpleStringPBEConfig();
            
            // JasyptConfig와 동일한 설정 사용
            config.setPassword(password);
            config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
            config.setKeyObtentionIterations("1000");
            config.setPoolSize("1");
            config.setProviderName("SunJCE");
            config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
            config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
            config.setStringOutputType("base64");
            
            encryptor.setConfig(config);
            return encryptor;
            
        } catch (SecurityException e) {
            // 보안 정책으로 인한 암호화기 생성 실패
            log.error("보안 정책으로 인한 암호화기 생성 실패: {}", e.getMessage());
            throw new RuntimeException("보안 정책으로 인해 암호화기를 생성할 수 없습니다", e);
            
        } catch (Exception e) {
            // 기타 암호화기 생성 오류
            log.error("암호화기 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("암호화기 생성 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 해시를 사용하지 않는 deterministic 암호화기를 생성합니다.
     * 같은 값을 암호화하면 항상 동일한 결과를 반환합니다.
     * 
     * @param password 암호화 키
     * @return PooledPBEStringEncryptor 인스턴스
     */
    /**
     * 결정적 암호화기를 만든다.
     *
     * @deprecated [W2-3] 이 설정은 세 가지가 겹쳐 사실상 보호가 되지 않는다.
     *             <ul>
     *               <li><b>{@code PBEWithMD5AndDES}</b> — DES는 유효 키 길이 56비트로 현대 기준에서
     *                   전수 탐색이 가능하고, MD5도 폐기된 해시다.</li>
     *               <li><b>고정 Salt</b>({@code AICC_DETERMINISTIC_SALT_2024}) — 같은 평문이 항상 같은
     *                   암호문이 되어 빈도분석·사전공격에 그대로 노출된다.</li>
     *               <li>인증(MAC)이 없어 변조를 탐지할 수 없다.</li>
     *             </ul>
     *             이 경로는 {@code EntityFieldConverter}·{@code MybatisParameterInterceptor}를 통해
     *             <b>실제 DB 컬럼</b>에 적용되고 있다.
     *
     *             <p>대체 방향: 저장은 AES-256-GCM({@code CoreUtils.aes256.encryptGcm}),
     *             동등 비교가 필요한 필드는 HMAC 기반 blind index({@code CoreUtils.aes256.blindIndex}).
     *             다만 기존 컬럼 값이 전부 이 방식으로 저장돼 있어, 전환에는 <b>재암호화 배치가
     *             선행</b>되어야 한다(W2-6). 그때까지 복호 호환을 위해 남긴다.
     */
    @Deprecated
    private static PooledPBEStringEncryptor createDeterministicEncryptor(String password) {
        try {
            PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            SimpleStringPBEConfig config = new SimpleStringPBEConfig();

            // 해시를 사용하지 않는 설정 (고정된 Salt 사용)
            config.setPassword(password);
            // IV가 필요하지 않은 알고리즘 사용 (PBEWithMD5AndDES는 IV가 필요 없음)
            config.setAlgorithm("PBEWithMD5AndDES");
            config.setKeyObtentionIterations("1000");
            config.setPoolSize("1");
            config.setProviderName("SunJCE");
            // 고정된 Salt를 사용하여 deterministic 암호화 (항상 같은 Salt 사용)
            StringFixedSaltGenerator saltGenerator = new StringFixedSaltGenerator("AICC_DETERMINISTIC_SALT_2024");
            config.setSaltGenerator(saltGenerator);
            // IV 생성기는 설정하지 않음 (PBEWithMD5AndDES는 IV가 필요 없음)
            config.setStringOutputType("base64");
            
            encryptor.setConfig(config);
            return encryptor;
            
        } catch (SecurityException e) {
            // 보안 정책으로 인한 암호화기 생성 실패
            log.error("보안 정책으로 인한 암호화기 생성 실패: {}", e.getMessage());
            throw new RuntimeException("보안 정책으로 인해 암호화기를 생성할 수 없습니다", e);
            
        } catch (Exception e) {
            // 기타 암호화기 생성 오류
            log.error("암호화기 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("암호화기 생성 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 설정 파일용 암호화된 문자열을 생성합니다. (ENC() 형태)
     * 
     * @param plainText 암호화할 평문
     * @param password 암호화 키
     * @return ENC(암호화된문자열) 형태의 문자열
     */
    public static String encryptForConfig(String plainText, String password) {
        String encrypted = encrypt(plainText, password);
        return "ENC(" + encrypted + ")";
    }
    
    /**
     * 현재 사용되는 기본 암호화 키를 반환합니다.
     * 
     * @return 기본 암호화 키
     */
    public static String getDefaultKey() {
        // 환경변수 우선 확인
        String envKey = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
        if (envKey != null && !envKey.trim().isEmpty()) {
            return envKey;
        }
        
        // JVM 시스템 프로퍼티 확인
        String systemKey = System.getProperty("jasypt.encryptor.password");
        if (systemKey != null && !systemKey.trim().isEmpty()) {
            return systemKey;
        }

        // [W2-3] 하드코딩 기본키 폴백 제거.
        // 기존에는 여기서 "aicc-default-key-2024" 를 돌려줬다. 저장소에 그대로 커밋된 값이라,
        // 환경변수를 주지 않으면 <b>알려진 키로 DB 필드가 암호화</b>됐다. 암호화한 셈만 치고
        // 실제로는 누구나 복호할 수 있는 상태였으므로, 조용히 넘어가지 않고 실패시킨다.
        throw new IllegalStateException(
                "Jasypt 마스터키가 설정되지 않았습니다. JASYPT_ENCRYPTOR_PASSWORD 환경변수 또는 "
                + "jasypt.encryptor.password 시스템 프로퍼티를 주입하십시오.");
    }
    
    /**
     * 문자열이 암호화된 형태인지 확인합니다.
     * 
     * @param text 확인할 문자열
     * @return 암호화된 형태이면 true, 아니면 false
     */
    public static boolean isEncrypted(String text) {
        return text != null && text.startsWith("ENC(") && text.endsWith(")");
    }
    
    /**
     * 기본 키로 암호화합니다.
     * 
     * @param plainText 암호화할 평문
     * @return 암호화된 문자열
     */
    public static String encryptWithDefaultKey(String plainText) {
        return encrypt(plainText, getDefaultKey());
    }
    
    /**
     * 기본 키로 복호화합니다.
     * 
     * @param encryptedText 암호화된 문자열
     * @return 복호화된 평문
     */
    public static String decryptWithDefaultKey(String encryptedText) {
        return decrypt(encryptedText, getDefaultKey());
    }
    
    /**
     * 기본 키로 설정 파일용 암호화를 수행합니다.
     * 
     * @param plainText 암호화할 평문
     * @return ENC(암호화된문자열) 형태
     */
    public static String encryptForConfigWithDefaultKey(String plainText) {
        return encryptForConfig(plainText, getDefaultKey());
    }
    
    public static String encryptField(String plainText){
        if (isEncrypted(plainText)) {
            log.debug("이미 암호화된 형태(ENC())이므로 재암호화하지 않음");
            return plainText;
        }
        return encryptDeterministicForConfigWithDefaultKey(plainText);
    }

    public static String decryptField(String encryptedText){
        if (!isEncrypted(encryptedText)) {
            log.debug("이미 복호화된 평문이므로 재복호화하지 않음");
            return encryptedText;
        }
        return decryptDeterministicWithDefaultKey(encryptedText);
    }
    
    /**
     * 해시를 사용하지 않는 deterministic 암호화를 수행합니다.
     * 같은 값을 암호화하면 항상 동일한 문자열을 반환합니다.
     * 
     * @param plainText 암호화할 평문
     * @param password 암호화 키
     * @return 암호화된 문자열 (같은 입력에 대해 항상 동일한 결과)
     */
    public static String encryptDeterministic(String plainText, String password) {
        try {
            if (plainText == null || plainText.trim().isEmpty()) {
                throw new IllegalArgumentException("암호화할 텍스트가 비어있습니다.");
            }
            
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("암호화 키가 비어있습니다.");
            }
            
            PooledPBEStringEncryptor encryptor = createDeterministicEncryptor(password);
            return encryptor.encrypt(plainText);
            
        } catch (IllegalArgumentException e) {
            // 입력 검증 실패는 그대로 전파
            throw e;
            
        } catch (EncryptionOperationNotPossibleException e) {
            // Jasypt 암호화 작업 실패 (키 오류, 알고리즘 문제 등)
            log.error("Jasypt 암호화 작업 실패: {}", e.getMessage());
            throw new RuntimeException("암호화 작업을 수행할 수 없습니다: " + e.getMessage(), e);
            
        } catch (SecurityException e) {
            // 보안 정책으로 인한 암호화 실패
            log.error("보안 정책으로 인한 암호화 실패: {}", e.getMessage());
            throw new RuntimeException("보안 정책으로 인해 암호화할 수 없습니다", e);
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("암호화 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("암호화 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 해시를 사용하지 않는 deterministic 암호화로 암호화된 문자열을 복호화합니다.
     * 
     * @param encryptedText 암호화된 문자열
     * @param password 암호화 키
     * @return 복호화된 평문
     */
    public static String decryptDeterministic(String encryptedText, String password) {
        try {
            if (encryptedText == null || encryptedText.trim().isEmpty()) {
                throw new IllegalArgumentException("복호화할 텍스트가 비어있습니다.");
            }
            
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("암호화 키가 비어있습니다.");
            }
            
            // ENC() 형태인 경우 괄호 안의 내용만 추출
            String textToDecrypt = encryptedText;
            if (encryptedText.startsWith("ENC(") && encryptedText.endsWith(")")) {
                textToDecrypt = encryptedText.substring(4, encryptedText.length() - 1);
            }
            
            PooledPBEStringEncryptor encryptor = createDeterministicEncryptor(password);
            return encryptor.decrypt(textToDecrypt);
            
        } catch (IllegalArgumentException e) {
            // 입력 검증 실패는 그대로 전파
            throw e;
            
        } catch (EncryptionOperationNotPossibleException e) {
            // Jasypt 복호화 작업 실패 (잘못된 키, 손상된 데이터 등)
            log.error("Jasypt 복호화 작업 실패: {}", e.getMessage());
            throw new RuntimeException("복호화 작업을 수행할 수 없습니다. 암호화 키나 데이터를 확인하세요: " + e.getMessage(), e);
            
        } catch (SecurityException e) {
            // 보안 정책으로 인한 복호화 실패
            log.error("보안 정책으로 인한 복호화 실패: {}", e.getMessage());
            throw new RuntimeException("보안 정책으로 인해 복호화할 수 없습니다", e);
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("복호화 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("복호화 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 기본 키로 해시를 사용하지 않는 deterministic 암호화를 수행합니다.
     * 
     * @param plainText 암호화할 평문
     * @return 암호화된 문자열 (같은 입력에 대해 항상 동일한 결과)
     */
    public static String encryptDeterministicWithDefaultKey(String plainText) {
        return encryptDeterministic(plainText, getDefaultKey());
    }
    
    /**
     * 기본 키로 해시를 사용하지 않는 deterministic 복호화를 수행합니다.
     * 
     * @param encryptedText 암호화된 문자열
     * @return 복호화된 평문
     */
    public static String decryptDeterministicWithDefaultKey(String encryptedText) {
        return decryptDeterministic(encryptedText, getDefaultKey());
    }
    
    /**
     * 설정 파일용 해시를 사용하지 않는 deterministic 암호화된 문자열을 생성합니다. (ENC() 형태)
     * 
     * @param plainText 암호화할 평문
     * @param password 암호화 키
     * @return ENC(암호화된문자열) 형태의 문자열 (같은 입력에 대해 항상 동일한 결과)
     */
    public static String encryptDeterministicForConfig(String plainText, String password) {
        String encrypted = encryptDeterministic(plainText, password);
        return "ENC(" + encrypted + ")";
    }
    
    /**
     * 기본 키로 설정 파일용 해시를 사용하지 않는 deterministic 암호화를 수행합니다.
     * 
     * @param plainText 암호화할 평문
     * @return ENC(암호화된문자열) 형태 (같은 입력에 대해 항상 동일한 결과)
     */
    public static String encryptDeterministicForConfigWithDefaultKey(String plainText) {
        return encryptDeterministicForConfig(plainText, getDefaultKey());
    }


    /**
     * 메인 메서드 - 명령행에서 암호화/복호화 도구로 사용
     * 
     * 사용법:
     * java buttle.apptpl.common.util.EncryptionTool encrypt "평문" [암호키]
     * java buttle.apptpl.common.util.EncryptionTool decrypt "암호화된문자열" [암호키]
     * java buttle.apptpl.common.util.EncryptionTool config "평문" [암호키]  (ENC() 형태로 출력)
     */
    public static void main(String[] args) {
        try {
            // 사용법 검사
            if (args.length < 1) {
                printUsage();
                System.exit(1);
            }
            
            String operation = args[0].toLowerCase();
            
            // key 명령어는 인자가 필요 없음
            if ("key".equals(operation)) {
                System.out.println ("현재 기본 암호화 키: " + getDefaultKey());
                System.out.println ("환경변수 JASYPT_ENCRYPTOR_PASSWORD: " + 
                         (System.getenv("JASYPT_ENCRYPTOR_PASSWORD") != null ? "설정됨" : "미설정"));
                System.out.println ("시스템 프로퍼티 jasypt.encryptor.password: " + 
                         (System.getProperty("jasypt.encryptor.password") != null ? "설정됨" : "미설정"));
                return;
            }
            
            // 다른 명령어들은 최소 2개 인자 필요
            if (args.length < 2) {
                printUsage();
                System.exit(1);
            }
            
            String text = args[1];
            String password = args.length > 2 ? args[2] : getDefaultKey();
            
            switch (operation) {
                case "encrypt":
                    String encrypted = encrypt(text, password);
                    System.out.print ("원본:[");
                    System.out.print (text);
                    System.out.println ("]");
                    System.out.print ("키: [");
                    System.out.print (password);
                    System.out.println ("]");
                    System.out.print ("암호화 결과:[");
                    System.out.print (encrypted);
                    System.out.println ("]");
                    System.out.print ("설정 파일에서 사용할 때:[");
                    System.out.print ("ENC(" + encrypted + ")");
                    System.out.println ("]");
                    break;
                    
                case "decrypt":
                    String decrypted = decrypt(text, password);
                    System.out.println ("복호화 결과:");
                    System.out.println (decrypted);
                    break;
                    
                case "config":
                    String configValue = encryptForConfig(text, password);
                    System.out.println ("설정 파일용 암호화 결과:");
                    System.out.println (configValue);
                    break;
                    
                case "test":
                    // 암호화/복호화 테스트
                    System.out.println ("암호화/복호화 테스트 실행...");
                    String testText = text;
                    String testEncrypted = encrypt(testText, password);
                    String testDecrypted = decrypt(testEncrypted, password);
                    
                    System.out.println ("원본: " + testText);
                    System.out.println ("암호화: " + testEncrypted);
                    System.out.println ("복호화: " + testDecrypted);
                    System.out.println ("설정파일용: ENC(" + testEncrypted + ")");
                    System.out.println ("테스트 " + (testText.equals(testDecrypted) ? "성공" : "실패"));
                    break;
                    
                case "check":
                    // 암호화 여부 확인
                    boolean isEncryptedFormat = isEncrypted(text);
                    System.out.println ("입력 텍스트: " + text);
                    System.out.println ("암호화된 형태: " + (isEncryptedFormat ? "예" : "아니오"));
                    if (isEncryptedFormat) {
                        try {
                            String decryptedResult = decrypt(text, password);
                            System.out.println ("복호화 결과: " + decryptedResult);
                        } catch (RuntimeException e) {
                            System.out.println ("복호화 실패: " + e.getMessage());
                        } catch (Exception e) {
                            System.out.println ("복호화 중 예상치 못한 오류: " + e.getMessage());
                        }
                    }
                    break;
                    
                case "encrypt-deterministic":
                    // 해시를 사용하지 않는 deterministic 암호화
                    String detEncrypted = encryptDeterministic(text, password);
                    System.out.print ("원본:[");
                    System.out.print (text);
                    System.out.println ("]");
                    System.out.print ("키: [");
                    System.out.print (password);
                    System.out.println ("]");
                    System.out.print ("암호화 결과 (deterministic):[");
                    System.out.print (detEncrypted);
                    System.out.println ("]");
                    System.out.print ("설정 파일에서 사용할 때:[");
                    System.out.print ("ENC(" + detEncrypted + ")");
                    System.out.println ("]");
                    break;
                    
                case "decrypt-deterministic":
                    // 해시를 사용하지 않는 deterministic 복호화
                    String detDecrypted = decryptDeterministic(text, password);
                    System.out.println ("복호화 결과 (deterministic):");
                    System.out.println (detDecrypted);
                    break;
                    
                case "config-deterministic":
                    // 해시를 사용하지 않는 deterministic 설정 파일용 암호화
                    String detConfigValue = encryptDeterministicForConfig(text, password);
                    System.out.println ("설정 파일용 암호화 결과 (deterministic):");
                    System.out.println (detConfigValue);
                    break;
                    
                case "test-deterministic":
                    // 해시를 사용하지 않는 deterministic 암호화/복호화 테스트
                    System.out.println ("Deterministic 암호화/복호화 테스트 실행...");
                    String detTestText = text;
                    String detTestEncrypted1 = encryptDeterministic(detTestText, password);
                    String detTestEncrypted2 = encryptDeterministic(detTestText, password);
                    String detTestDecrypted = decryptDeterministic(detTestEncrypted1, password);
                    
                    System.out.println ("원본: " + detTestText);
                    System.out.println ("암호화 1회: " + detTestEncrypted1);
                    System.out.println ("암호화 2회: " + detTestEncrypted2);
                    System.out.println ("동일성 검증: " + (detTestEncrypted1.equals(detTestEncrypted2) ? "성공 (동일)" : "실패 (다름)"));
                    System.out.println ("복호화: " + detTestDecrypted);
                    System.out.println ("설정파일용: ENC(" + detTestEncrypted1 + ")");
                    System.out.println ("테스트 " + (detTestText.equals(detTestDecrypted) ? "성공" : "실패"));
                    break;
                    
                default:
                    System.err.println("잘못된 작업입니다: " + operation);
                    printUsage();
                    System.exit(1);
            }
            
        } catch (IllegalArgumentException e) {
            // 입력 검증 오류 (사용자 오류)
            System.err.println("입력 오류: " + e.getMessage());
            printUsage();
            System.exit(1);
            
        } catch (RuntimeException e) {
            // 암호화/복호화 관련 런타임 오류
            System.err.println("암호화 처리 오류: " + e.getMessage());
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            System.exit(1);
            
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * 사용법을 출력합니다.
     */
    private static void printUsage() {
        System.out.println ("====================================");
        System.out.println ("     AICC Backend 암호화 도구");
        System.out.println ("====================================");
        System.out.println ("사용법:");
        System.out.println ("  java buttle.apptpl.common.util.EncryptionTool <operation> <text> [password]");
        System.out.println ("");
        System.out.println ("Operations:");
        System.out.println ("  encrypt              - 텍스트 암호화");
        System.out.println ("  decrypt              - 텍스트 복호화");
        System.out.println ("  config               - 설정 파일용 암호화 (ENC() 형태)");
        System.out.println ("  test                 - 암호화/복호화 테스트");
        System.out.println ("  check                - 텍스트가 암호화된 형태인지 확인");
        System.out.println ("  key                  - 현재 사용 중인 암호화 키 정보 표시");
        System.out.println ("  encrypt-deterministic - 해시를 사용하지 않는 deterministic 암호화 (같은 값은 항상 동일한 결과)");
        System.out.println ("  decrypt-deterministic - 해시를 사용하지 않는 deterministic 복호화");
        System.out.println ("  config-deterministic - 해시를 사용하지 않는 deterministic 설정 파일용 암호화");
        System.out.println ("  test-deterministic   - 해시를 사용하지 않는 deterministic 암호화/복호화 테스트");
        System.out.println ("");
        System.out.println ("Examples:");
        System.out.println ("  java ... encrypt \"aicc_password\"");
        System.out.println ("  java ... encrypt \"aicc_password\" \"my-secret-key\"");
        System.out.println ("  java ... decrypt \"ENC(암호화된문자열)\"");
        System.out.println ("  java ... config \"aicc_password\"");
        System.out.println ("  java ... test \"hello world\"");
        System.out.println ("  java ... check \"ENC(암호화된문자열)\"");
        System.out.println ("  java ... key");
        System.out.println ("  java ... encrypt-deterministic \"aicc_password\"");
        System.out.println ("  java ... decrypt-deterministic \"ENC(암호화된문자열)\"");
        System.out.println ("  java ... config-deterministic \"aicc_password\"");
        System.out.println ("  java ... test-deterministic \"hello world\"");
        System.out.println ("");
        System.out.println ("기본 암호화 키: " + getDefaultKey());
        System.out.println ("환경변수 JASYPT_ENCRYPTOR_PASSWORD로 키 변경 가능");
        System.out.println ("시스템 프로퍼티 -Djasypt.encryptor.password=키값 으로도 변경 가능");
    }
}