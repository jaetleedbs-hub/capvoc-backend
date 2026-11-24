package buttle.apptpl.common.config.caffeine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public enum CacheType {

    JWT_REFRESH_TOKEN("jwtRefreshToken", 10 * 60, 300),
    SESSION_USER_INFO("jwtUserInfo", 10 * 60, 300), // 로그인 사용자 수만큼
    CODE_INFO("codeInfo", 10 * 60, 100), // 공통코드 조회 시 대분류 만큼
    ENV_INFO("envInfo", 5 * 60, 30), // 환경변수 조회 시 조회조건 만큼
    MENU_AUTHRT_INFO("menuAuthrtInfo", 10 * 60, 50), // 메뉴 권한 조회 시 조회조건 만큼
    PRGRM_AUTHRT_INFO("prgrmAuthrtInfo", 10 * 60, 50), // 프로그램 권한 조회 시 조회조건 만큼
    USER_LONGPOLL_INFO("noticeLongpollInfo", 30 * 60, 100); // 공지사항 롱폴링 시 사용자 수만큼
    // [A4-2] USER_SSO_INFO 제거 — 유일한 사용처였던 getUserUniqueKey(외부 URL SSO 토큰)가
    //  죽은 경로라 함께 걷어냈다. CaffeineConfig 가 values() 를 순회해 캐시를 만들므로
    //  상수를 빼면 그 캐시도 생성되지 않는다.


    /**
     * 프로젝트명 (최상위 계층)
     */
    private static final String PROJECT_NAME = "aicc-admin";

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
    
    public String getCacheNameWithCompany() {
        return PROJECT_NAME + "::" + cacheName;
    }

    

} 
