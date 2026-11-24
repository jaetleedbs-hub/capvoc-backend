# 회원사 API 연동 모듈

## 📋 개요

회원사별로 등록된 외부 API를 호출하기 위한 공통 어댑터 모듈입니다.

## 📁 디렉토리 구조

```
smartcc/common/adapter/
├── CompanyApiAdapter.java              # API 호출 어댑터 (핵심)
├── config/
│   └── CompanyApiConfig.java           # WebClient 및 캐시 설정
├── dto/
│   └── CompanyApiInfo.java             # 회원사 API 정보 DTO
├── exception/
│   └── CompanyApiException.java        # API 호출 예외
├── mapper/
│   ├── CompanyApiMapper.java           # MyBatis Mapper 인터페이스
│   └── CompanyApiMapper.xml            # MyBatis Mapper XML
├── service/
│   └── CompanyApiCacheService.java     # 캐시 서비스
├── web/
│   └── CompanyApiController.java       # 관리 API Controller
└── db/
    └── alter_cmpt_cmpy_inf.sql         # 테이블 ALTER DDL
```

## 🚀 사용 방법

### 1. DB 테이블 컬럼 추가

기존 `cmpt_cmpy_inf` 테이블에 API 연동 컬럼을 추가합니다.

```sql
-- db/alter_cmpt_cmpy_inf.sql 실행

-- API 서버 URL 컬럼 추가
ALTER TABLE cmpt_cmpy_inf 
ADD COLUMN api_svr_url VARCHAR(500) NULL COMMENT 'API 서버 URL';

-- API 인증 토큰 컬럼 추가
ALTER TABLE cmpt_cmpy_inf 
ADD COLUMN api_auth_token VARCHAR(1000) NULL COMMENT 'API 인증 토큰';

-- API 타임아웃 컬럼 추가
ALTER TABLE cmpt_cmpy_inf 
ADD COLUMN api_timeout_sec INT NULL COMMENT 'API 타임아웃(초)';
```

### 2. 회원사 API 정보 등록

```sql
-- 기존 회원사에 API 정보 추가
UPDATE cmpt_cmpy_inf
SET 
    api_svr_url = 'https://api.yourcompany.com',
    api_auth_token = 'YOUR_BEARER_TOKEN',
    api_timeout_sec = 30
WHERE cmpy_id = 'YOUR_COMPANY_ID';
```

### 3. Service에서 CompanyApiAdapter 사용

#### GET 요청

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final CompanyApiAdapter companyApiAdapter;
    
    public UserListResponse getUserList(String companyId, UserListRequest request) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userName", request.getUserName());
        queryParams.put("page", request.getPage());
        
        return companyApiAdapter.get(
            companyId,
            "/api/v1/users",  // AICC-API 컬럼 값
            queryParams,
            UserListResponse.class
        );
    }
}
```

#### POST 요청

```java
public UserCreateResponse createUser(String companyId, UserCreateRequest request) {
    return companyApiAdapter.post(
        companyId,
        "/api/v1/users",
        request,
        UserCreateResponse.class
    );
}
```

#### PUT 요청

```java
public UserUpdateResponse updateUser(String companyId, String userId, UserUpdateRequest request) {
    return companyApiAdapter.put(
        companyId,
        "/api/v1/users/" + userId,
        request,
        UserUpdateResponse.class
    );
}
```

#### DELETE 요청

```java
public void deleteUser(String companyId, String userId) {
    companyApiAdapter.delete(
        companyId,
        "/api/v1/users/" + userId,
        Void.class
    );
}
```

## ⚙️ 설정

### WebClient 설정 (CompanyApiConfig.java)

- **연결 타임아웃**: 10초
- **응답 타임아웃**: 30초
- **읽기/쓰기 타임아웃**: 30초

### 캐시 설정 (Caffeine)

- **최대 크기**: 1000개
- **만료 시간**: 1시간 (마지막 접근 후)
- **통계 기록**: 활성화

## 🔒 보안

### 1. 인증 방식

- **Bearer Token**: `api_auth_token` 컬럼에 토큰 저장
- **API Key**: `api_key` 컬럼에 키 저장

### 2. HTTPS 통신

- 모든 API 호출은 HTTPS를 사용해야 합니다.
- HTTP는 보안상 권장하지 않습니다.

### 3. 민감 정보 보호

- 토큰 및 API Key는 암호화하여 저장하는 것을 권장합니다.
- 로그에 민감 정보가 노출되지 않도록 주의합니다.

## 📊 캐시 관리

### 캐시 갱신 API

```bash
# 특정 회원사 캐시 갱신
POST /api/admin/company-api/{companyId}/refresh-cache

# 전체 캐시 초기화
POST /api/admin/company-api/clear-all-cache
```

### 프로그래밍 방식 캐시 갱신

```java
@Autowired
private CompanyApiCacheService companyApiCacheService;

// 특정 회원사 캐시 갱신
companyApiCacheService.refreshCache("CMPY001");

// 전체 캐시 초기화
companyApiCacheService.clearAllCache();
```

## ⚠️ 예외 처리

### CompanyApiException

회원사 API 호출 시 발생하는 예외를 처리합니다.

```java
try {
    UserListResponse response = companyApiAdapter.get(...);
} catch (CompanyApiException e) {
    log.error("회원사 API 호출 실패 - companyId: {}, statusCode: {}", 
        e.getCompanyId(), e.getStatusCode());
    // 예외 처리 로직
}
```

### GlobalExceptionHandler

전역 예외 처리기에서 자동으로 처리됩니다:

- **HTTP 502 Bad Gateway** 응답
- 사용자에게 친화적인 오류 메시지 제공

## 🧪 테스트

### 단위 테스트 예시

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private CompanyApiAdapter companyApiAdapter;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testGetUserList() {
        // Given
        String companyId = "CMPY001";
        UserListResponse expectedResponse = new UserListResponse();
        when(companyApiAdapter.get(any(), any(), any(), any()))
            .thenReturn(expectedResponse);
        
        // When
        UserListResponse result = userService.getUserList(companyId, new UserListRequest());
        
        // Then
        assertNotNull(result);
    }
}
```

## 📝 체크리스트

### 개발 시 확인사항

- [ ] 회원사 API 정보가 DB에 등록되었는가?
- [ ] AICC-API 컬럼 값을 정확히 사용했는가?
- [ ] 응답 DTO가 회원사 API 스펙과 일치하는가?
- [ ] 예외 처리가 적절히 구현되었는가?
- [ ] 단위 테스트를 작성했는가?

### 배포 전 확인사항

- [ ] 회원사 API 서버와 네트워크 연결이 가능한가?
- [ ] 인증 정보(Token, API Key)가 정확한가?
- [ ] 타임아웃 설정이 적절한가?
- [ ] 캐시가 정상 동작하는가?

## 🔗 참고 문서

- [회원사 API 연동 구현 가이드](../../../../../../../dev-tools/cursor/auto/impl/회원사_API_연동_구현_가이드.md)
- [Spring WebClient 문서](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client)
- [Caffeine Cache 문서](https://github.com/ben-manes/caffeine/wiki)

## 📞 문의

기술 지원: ID-ABO 개발팀

---

**문서 버전**: 1.0  
**최종 수정일**: 2026-01-15
