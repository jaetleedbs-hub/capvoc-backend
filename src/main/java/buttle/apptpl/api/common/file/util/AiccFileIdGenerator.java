package buttle.apptpl.api.common.file.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 파일 ID 및 그룹 ID 생성 유틸리티
 * 
 * 파일 ID 형식: {시스템코드}-{FILE_ID_PREFIX}-{UUID}
 * 예시: admin-att-550e8400-e29b-41d4-a716-446655440000
 * 
 * 그룹 ID 형식: {시스템코드}-{FILE_GROUP_ID_PREFIX}-{UUID}
 * 예시: admin-attg-550e8400-e29b-41d4-a716-446655440000
 */
@Component
public class AiccFileIdGenerator {

    private static final String FILE_ID_PREFIX = "att";
    private static final String FILE_GROUP_ID_PREFIX = "attg";

    @Value("${system.code}")
    private String systemCode;

    @Value("${system.company-id}")
    private String companyId;

    private static AiccFileIdGenerator instance;

    public AiccFileIdGenerator() {
        instance = this;
    }

    /**
     * 현재 시스템 설정에 따른 파일 ID 생성
     * 
     * @return 파일 ID (예: admin-att-550e8400-e29b-41d4-a716-446655440000)
     */
    public String generateFileId() {
        return systemCode + "-" + FILE_ID_PREFIX + "-" + UUID.randomUUID().toString();
    }


    /**
     * 현재 시스템 설정에 따른 그룹 ID 생성
     * 
     * @return 그룹 ID (예: admin-attg-550e8400-e29b-41d4-a716-446655440000)
     */
    public String generateGroupId() {
        return systemCode + "-" + FILE_GROUP_ID_PREFIX + "-" + UUID.randomUUID().toString();
    }

    /**
     * AICC-ADMIN용 파일 ID 생성 (정적 메서드 유지 - 내부적으로 instance 사용)
     * 
     * @return 파일 ID (예: admin-att-550e8400-e29b-41d4-a716-446655440000)
     */
    public static String generateAdminFileId() {
        if (instance == null) {
            throw new RuntimeException("AiccFileIdGenerator가 초기화되지 않았습니다.");
        }
        return instance.generateFileId();
    }

    /**
     * AICC-ADMIN용 그룹 ID 생성 (정적 메서드 유지 - 내부적으로 instance 사용)
     * 
     * @return 그룹 ID (예: admin-attg-550e8400-e29b-41d4-a716-446655440000)
     */
    public static String generateAdminGroupId() {
        if (instance == null) {
            throw new RuntimeException("AiccFileIdGenerator가 초기화되지 않았습니다.");
        }
        return instance.generateGroupId();
    }

}
