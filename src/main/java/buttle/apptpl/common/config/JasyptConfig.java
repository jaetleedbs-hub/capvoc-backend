package buttle.apptpl.common.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jasypt 암호화 설정 클래스
 * 데이터베이스 접속 정보 및 민감한 설정 값들을 암호화하여 관리
 * 
 * 암호화 키는 다음 우선순위로 설정됩니다:
 * 1. 환경변수 JASYPT_ENCRYPTOR_PASSWORD
 * 2. JVM 시스템 프로퍼티 jasypt.encryptor.password
 * 3. application.yml의 jasypt.encryptor.password (기본값)
 * 
 * @author AICC Development Team
 * @since 2024-01-15
 */
@Configuration
@EnableEncryptableProperties
public class JasyptConfig {
    
    /**
     * 암호화 키 (환경변수 우선, 기본값 제공)
     * 운영 환경에서는 반드시 환경변수로 설정하세요
     */
    @Value("${jasypt.encryptor.password:aicc-default-key-2024}")
    private String encryptorPassword;

    /**
     * Jasypt StringEncryptor Bean 설정
     * PBE (Password-Based Encryption) 알고리즘 사용
     * 
     * @return StringEncryptor 인스턴스
     */
    @Bean("jasyptStringEncryptor")
    @Primary
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        
        // 암호화 키 설정 (환경변수 우선)
        String password = getEncryptionPassword();
        config.setPassword(password);
        
        // 암호화 알고리즘 설정
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        
        // 키 획득 반복 횟수 (보안 강화)
        config.setKeyObtentionIterations("1000");
        
        // 연결 풀 크기 (성능 최적화)
        config.setPoolSize("1");
        
        // 공급자 이름
        config.setProviderName("SunJCE");
        
        // Salt 생성기 클래스명
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        
        // IV 생성기 클래스명 (AES 사용 시 필요)
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        
        // 문자열 출력 타입
        config.setStringOutputType("base64");
        
        encryptor.setConfig(config);
        return encryptor;
    }
    
    /**
     * 암호화 키를 안전하게 가져오는 메서드
     * 우선순위: 환경변수 > JVM 시스템 프로퍼티 > 설정 파일
     * 
     * @return 암호화 키
     */
    private String getEncryptionPassword() {
        // 1. 환경변수 확인
        String envPassword = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
        if (envPassword != null && !envPassword.trim().isEmpty()) {
            return envPassword;
        }
        
        // 2. JVM 시스템 프로퍼티 확인
        String systemPassword = System.getProperty("jasypt.encryptor.password");
        if (systemPassword != null && !systemPassword.trim().isEmpty()) {
            return systemPassword;
        }
        
        // 3. 설정 파일의 기본값 사용
        return encryptorPassword;
    }
}