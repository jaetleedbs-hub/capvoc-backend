# AICC Backend 개발 가이드

## 1. 개요

AICC Backend는 Spring Boot 3.4.1 기반의 SCRM(Smart Customer Relationship Management) 시스템 백엔드 서비스입니다.

### 기술 스택
- **Java**: OpenJDK 17
- **Framework**: Spring Boot 3.4.1
- **ORM**: JPA + MyBatis 3.0.4
- **Database**: PostgreSQL 42.7.4, H2 (개발용)
- **Security**: Spring Security + JWT
- **API Documentation**: SpringDoc OpenAPI 2.8.4
- **Build Tool**: Gradle 7.x
- **WebClient**: Spring WebFlux (외부 API 통신)

## 2. 환경

### 2.1. 디렉토리(package) 표준

프로젝트의 모든 API 관련 소스는 `smartcc.api` 하위에 위치합니다.

```
src/main/java/smartcc/api/
├── {module}/                    # 모듈별 패키지 (asm, cns, cmp, com, sys, sup, sts, sph)
│   ├── dto/                     # Data Transfer Object
│   ├── mapper/                  # MyBatis Mapper Interface & XML
│   ├── service/                 # Service Layer
│   ├── vo/                      # Value Object (MyBatis 결과 매핑용)
│   └── web/                     # Controller Layer
├── common/                      # 공통 컴포넌트
│   ├── components/              # 공통 서비스 컴포넌트
│   └── polling/                 # 폴링 관련 컴포넌트
└── sample/                      # 개발 샘플 코드
```

### 2.2. 구성요소 명명규칙

#### 프로그램 ID 규칙
- **형식**: `{모듈코드}{업무코드}{일련번호}`
- **예시**: `SYS0010`, `CNS1020`, `ASM2030`
- **모듈코드**: 
  - SYS: 시스템관리
  - CNS: 상담관리  
  - ASM: 평가관리
  - CMP: 캠페인관리
  - COM: 공통
  - SUP: 지원
  - STS: 통계
  - SPH: 음성

#### API 규칙
- **기본 경로**: `/api/{프로그램id소문자}`
- **메소드별 경로**:
  - 조회: `/r{순번}` (예: `/r01`, `/r02`)
  - 저장/수정: `/u{순번}` (예: `/u01`, `/u02`)
  - 삭제: `/d{순번}` (예: `/d01`)

#### Class 명명 규칙
- **Entity**: `{테이블명}Entity` (예: `CmptUserEntity`)
- **Repository**: `{테이블명}Repo` (예: `CmptUserRepo`)
- **Mapper**: `{프로그램ID}Mapper` (예: `SYS0010Mapper`)
- **Service**: `{프로그램ID}Service` (예: `SYS0010Service`)
- **Controller**: `{프로그램ID}Controller` (예: `SYS0010Controller`)
- **DTO**: `{프로그램ID}{순번}Dto` (예: `SYS0010001Dto`)
- **VO**: `{프로그램ID}{순번}Vo` (예: `SYS0010001Vo`)

#### Method 명명 규칙
- **조회**: `select{대상}` (예: `selectUserList`, `selectUser`)
- **저장**: `insert{대상}` (예: `insertUser`)
- **수정**: `update{대상}` (예: `updateUser`)
- **삭제**: `delete{대상}` (예: `deleteUser`)

#### MyBatis Mapper 규칙
- **XML 파일**: Mapper 클래스와 동일한 패키지에 위치
- **namespace**: Mapper 인터페이스의 풀 패키지명
- **resultType**: VO 클래스 사용

## 3. 컴포넌트 설명

### 3.1. JPA Entity

#### 기본 구조
```java
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "테이블명")
public class CmptUserEntity extends BaseAuditingEntity implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;
    
    // 연관관계 매핑 예시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ognz_cd", insertable = false, updatable = false)
    private CmptOgnzEntity cmptOgnz;
}
```

#### 테이블 생성/변경 시 규칙
1. **BaseAuditingEntity 상속**: 생성일시, 수정일시, 생성자, 수정자 자동 관리
2. **@Builder 패턴 사용**: 객체 생성의 일관성 보장
3. **연관관계 매핑**: `insertable = false, updatable = false` 설정으로 읽기 전용
4. **FetchType.LAZY**: 성능 최적화를 위한 지연 로딩

### 3.2. MyBatis Mapper

#### 소스 위치 규칙
- **Mapper 클래스와 XML 파일은 반드시 같은 패키지에 위치**

#### Mapper Interface 예시
```java
@Mapper
public interface SMP0000Mapper {
    List<SMP0000002Vo> selectCmptCdList(SMP0000001Vo param);
    SMP0000002Vo selectCmptCd(String cdGroup);
    List<CmptOgnzEntity> selectCmptOgnzList(SMP0000003Vo param);
}
```

#### XML 매핑 파일 예시
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="smartcc.api.sample.mapper.SMP0000Mapper">
    
    <select id="selectCmptCdList" parameterType="smartcc.api.sample.vo.SMP0000001Vo" 
            resultType="smartcc.api.sample.vo.SMP0000002Vo">
        SELECT 
            cd.cd,
            cd.cd_group as cdGroup,
            cd.cd_nm as cdNm
        FROM aicc.cmpt_cd cd
        WHERE 1=1
        <if test="cdGroup != null and cdGroup != ''">
            AND cd.cd_group = #{cdGroup}
        </if>
        ORDER BY cd.sort_sn
    </select>
    
</mapper>
```

#### VO 생성 규칙
- **용도**: MyBatis 결과 매핑 전용
- **위치**: `{module}/vo/` 패키지
- **명명**: `{프로그램ID}{순번}Vo`
- **특징**: 단순 데이터 전달 객체, 비즈니스 로직 없음

### 3.3. Service 규칙

#### @Service 명명
```java
@Service
@RequiredArgsConstructor
@Transactional  // 클래스 레벨에 적용
@Slf4j
public class SMP0000Service {
    
    private final SMP0000Mapper mapper;
    private final CmptCdRepo cmptCdRepo;
    private final ModelConveter modelConverter;
}
```

#### Model Converter 사용
```java
// DTO -> VO 변환
SMP0000001Vo vo = modelConverter.map(dto, SMP0000001Vo.class);

// VO -> DTO 변환 (단건)
return modelConverter.map(mapper.selectCmptCd(cdGroup), SMP0000002Dto.class);

// List 변환
return modelConverter.mapList(mapper.selectCmptCdList(vo), SMP0000002Dto.class);
```

#### @Transactional 사용 표준
1. **클래스 레벨**: `@Transactional` 적용
2. **읽기 전용 메소드**: `@Transactional(readOnly = true)` 명시
3. **트랜잭션 불필요 메소드**: 어노테이션 생략

```java
@Service
@Transactional  // 클래스 레벨 적용
public class CNS0160Service {
    
    @Transactional(readOnly = true)  // 읽기 전용
    public List<CNS0160001Dto> selectCallbackList(CNS0160001Dto dto) {
        // 조회 로직
    }
    
    public void insertCallback(CNS0160002Dto dto) {
        // 저장 로직 (클래스 레벨 @Transactional 적용)
    }
}
```

### 3.4. DTO 규칙

#### 기본 구조
```java
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CNS0030001Dto extends BaseDto {
    
    @Schema(description = "콜백 시퀀스ID", type = "integer")
    private Integer callbakSid;
    
    @Masking(MaskingType.NAME)
    @Schema(description = "고객 명", type = "string")
    private String custNm;
    
    @Masking(MaskingType.PHONE_NUMBER)
    @Schema(description = "콜백 전화 번호", type = "string")
    private String callbakTelNo;
}
```

#### @Masking 사용 설명
- **용도**: 개인정보 마스킹 처리
- **타입**:
  - `MaskingType.NAME`: 이름 마스킹 (홍*동)
  - `MaskingType.PHONE_NUMBER`: 전화번호 마스킹 (010-****-1234)
  - 기타 필요에 따라 확장 가능

#### Swagger-UI를 위한 Annotation
- **@Schema**: 필드 설명, 타입, 필수여부, 예시값 정의
- **requiredMode**: `Schema.RequiredMode.REQUIRED` (필수), `NOT_REQUIRED` (선택)
- **example**: API 문서에 표시될 예시값

#### BaseDto 상속
- **목적**: 공통 필드 제공 (recid 등)
- **필수**: 모든 DTO는 BaseDto 상속
- **SuperBuilder**: 상속 구조에서 Builder 패턴 지원

### 3.5. Controller 규칙

#### 기본 구조
```java
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/smp0000")
@Tag(name = "SMP0000Controller", description = "프로그램 샘플")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", 
             content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class SMP0000Controller {
    
    private final SMP0000Service service;
}
```

#### Swagger-UI용 @Annotation
- **@Tag**: 컨트롤러 그룹핑 및 설명
- **@Operation**: 메소드별 API 설명
- **@ApiResponse**: 응답 코드별 설명
- **@ParameterObject**: 파라미터 객체 문서화

#### RequestMapping 규칙
- **기본 경로**: `/api/{프로그램id소문자}`
- **메소드 매핑**: `@PostMapping` 주로 사용
- **경로 패턴**: `/r{순번}` (조회), `/u{순번}` (저장/수정), `/d{순번}` (삭제)

#### Service Instance 주입
```java
@RequiredArgsConstructor  // final 필드 생성자 주입
private final SMP0000Service service;
```

### 3.6. WebClient 활용

#### 새로운 API 추가 시
1. **설정 파일 추가**: `application.yml`에 서버 정보 등록
2. **WebClient 사용**: `WebClientAdaptor`를 통한 클라이언트 생성
3. **서비스 구현**: 외부 API 호출 로직 작성

#### 새로운 WebClient 대상 서버 추가 절차
1. **application.yml 설정**:
```yaml
webclient:
  servers:
    new-server:
      base-url: https://api.example.com
      timeout-millis: 30000
      max-connections: 100
      ssl-trust-all: false
```

2. **WebClient 사용**:
```java
@Service
public class ExternalApiService {
    
    private final WebClientAdaptor webClientAdaptor;
    
    public ResponseDto callExternalApi(RequestDto request) {
        WebClient webClient = webClientAdaptor.getWebClient("new-server");
        
        return webClient.post()
                .uri("/api/endpoint")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ResponseDto.class)
                .block();
    }
}
```

### 3.7. 페이징 가이드

#### CorePaginationParam 사용
```java
public class SearchDto extends CorePaginationParam {
    // 검색 조건 필드들
    private String searchKeyword;
    
    // CorePaginationParam에서 상속받는 필드들:
    // - Long page (기본값: 1)
    // - Long itemsPerPage (기본값: 15)
}
```

#### MyBatis에서 페이징 처리
```xml
<select id="selectListWithPaging" parameterType="SearchDto" resultType="ResultVo">
    SELECT * FROM (
        SELECT ROW_NUMBER() OVER (ORDER BY create_dt DESC) as rn, *
        FROM your_table
        WHERE 1=1
        <!-- 검색 조건들 -->
    ) t
    WHERE rn BETWEEN #{offset} AND #{limit}
</select>
```

### 3.8. Validation 표준

#### ValidationException 사용
```java
// 필수 항목 검증
if(ObjectUtils.isEmpty(dto.getCdGroup())){
    throw new InvalidationException("필수항목 코드그룹이 전달되지 않았습니다.");
}

// 비즈니스 규칙 검증
if(!cmptCdGroupRepo.existsById(dto.getCdGroup())) {
    throw new InvalidationException(
        String.format("코드에 대한 코드 그룹(%s)이 존재하지 않습니다.", dto.getCdGroup())
    );
}
```

#### Bean Validation 활용
```java
@NotNull(message = "사용자 ID는 필수입니다")
@Size(max = 50, message = "사용자 ID는 50자를 초과할 수 없습니다")
private String userId;

@Email(message = "올바른 이메일 형식이 아닙니다")
private String email;
```

## 4. 개발 예시

### 4.1. CRUD 프로그램 개발 절차

#### 1단계: Entity 및 Repository 생성
```java
// 1. Entity 생성
@Entity
@Table(name = "sample_table")
public class SampleEntity extends BaseAuditingEntity {
    @Id
    private String sampleId;
    private String sampleName;
    // ... 기타 필드
}

// 2. Repository 생성
public interface SampleRepo extends JpaRepository<SampleEntity, String> {
    List<SampleEntity> findByUseYn(String useYn);
}
```

#### 2단계: DTO/VO 생성
```java
// 검색 조건 DTO
@SuperBuilder
@Getter @Setter
public class SMP0001001Dto extends BaseDto {
    private String sampleId;
    private String sampleName;
}

// 결과 VO
@Getter @Setter
public class SMP0001001Vo {
    private String sampleId;
    private String sampleName;
    private String createDt;
}
```

#### 3단계: Mapper 생성
```java
// Mapper Interface
@Mapper
public interface SMP0001Mapper {
    List<SMP0001001Vo> selectSampleList(SMP0001001Dto param);
    SMP0001001Vo selectSample(String sampleId);
}
```

```xml
<!-- Mapper XML -->
<mapper namespace="smartcc.api.sample.mapper.SMP0001Mapper">
    <select id="selectSampleList" resultType="smartcc.api.sample.vo.SMP0001001Vo">
        SELECT sample_id as sampleId, sample_name as sampleName
        FROM sample_table
        WHERE use_yn = 'Y'
        <if test="sampleName != null">
            AND sample_name LIKE '%' || #{sampleName} || '%'
        </if>
    </select>
</mapper>
```

#### 4단계: Service 생성
```java
@Service
@RequiredArgsConstructor
@Transactional
public class SMP0001Service {
    
    private final SMP0001Mapper mapper;
    private final SampleRepo sampleRepo;
    private final ModelConveter modelConverter;
    
    @Transactional(readOnly = true)
    public List<SMP0001001Dto> selectSampleList(SMP0001001Dto dto) {
        return modelConverter.mapList(
            mapper.selectSampleList(dto), 
            SMP0001001Dto.class
        );
    }
    
    public void insertSample(SMP0001001Dto dto) {
        SampleEntity entity = modelConverter.map(dto, SampleEntity.class);
        sampleRepo.save(entity);
    }
}
```

#### 5단계: Controller 생성
```java
@RestController
@RequestMapping("/api/smp0001")
@RequiredArgsConstructor
@Tag(name = "SMP0001", description = "샘플 관리")
public class SMP0001Controller {
    
    private final SMP0001Service service;
    
    @PostMapping("/r01")
    @Operation(summary = "샘플 목록 조회")
    public List<SMP0001001Dto> selectSampleList(@RequestBody SMP0001001Dto dto) {
        return service.selectSampleList(dto);
    }
    
    @PostMapping("/u01")
    @Operation(summary = "샘플 저장")
    public void insertSample(@RequestBody SMP0001001Dto dto) {
        service.insertSample(dto);
    }
}
```

### 4.2. API 프로그램 개발 절차

#### 1단계: 외부 API 설정
```yaml
# application.yml
webclient:
  servers:
    external-api:
      base-url: https://external-api.com
      timeout-millis: 30000
```

#### 2단계: Request/Response DTO 생성
```java
@Getter @Setter
public class ExternalApiRequestDto {
    private String requestId;
    private String data;
}

@Getter @Setter  
public class ExternalApiResponseDto {
    private String responseId;
    private String result;
}
```

#### 3단계: API Service 생성
```java
@Service
@RequiredArgsConstructor
public class ExternalApiService {
    
    private final WebClientAdaptor webClientAdaptor;
    
    public ExternalApiResponseDto callExternalApi(ExternalApiRequestDto request) {
        WebClient webClient = webClientAdaptor.getWebClient("external-api");
        
        return webClient.post()
                .uri("/api/data")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExternalApiResponseDto.class)
                .timeout(Duration.ofSeconds(30))
                .block();
    }
}
```

#### 4단계: Controller에서 API 호출
```java
@RestController
@RequestMapping("/api/outbound")   // ← "/api/external" 을 쓰지 말 것 (아래 주의)
public class ExternalApiController {
    
    private final ExternalApiService externalApiService;
    
    @PostMapping("/call")
    public ExternalApiResponseDto callApi(@RequestBody ExternalApiRequestDto request) {
        return externalApiService.callExternalApi(request);
    }
}
```

> ⚠️ **`/api/external` 경로는 쓰지 마십시오.**
> 이 예시가 오랫동안 `/api/external` 로 되어 있었고, 그 경로는 `JwtAuthFilter` 와
> `FwAuthorizationManager` **양쪽 예외 목록**에 들어 있었습니다. 즉 이 가이드를 따라 만들면
> **인증도 인가도 없는 API** 가 생기고, 만든 사람은 그 사실을 알 방법이 없었습니다.
> 2026-08-16 에 두 예외 항목을 제거했습니다(당시 그 경로의 컨트롤러는 0건).
>
> **새 API 는 별도 조치 없이 인증·인가를 받습니다.** 예외가 꼭 필요하면
> 그 이유를 남기고 키 인증을 함께 붙이십시오 — 예외 목록에 한 줄 넣는 것으로 끝내지 마십시오.

---

## 주의사항

1. **트랜잭션 관리**: Service 클래스 레벨에 `@Transactional` 적용, 읽기 전용 메소드는 `@Transactional(readOnly=true)` 명시
2. **보안**: 개인정보 필드에는 반드시 `@Masking` 어노테이션 적용
3. **API 문서화**: 모든 Controller 메소드에 Swagger 어노테이션 적용
4. **예외 처리**: 비즈니스 검증 실패 시 `InvalidationException` 사용
5. **성능**: JPA 연관관계는 `FetchType.LAZY` 사용
6. **코드 품질**: ModelConverter를 통한 객체 변환으로 일관성 유지

이 가이드를 따라 개발하면 일관성 있고 유지보수가 용이한 백엔드 서비스를 구축할 수 있습니다.
