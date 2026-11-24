package buttle.apptpl.common.mybatis;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;
import org.springframework.core.annotation.AnnotationUtils;
import buttle.apptpl.common.security.annotation.Encrypt;
import buttle.apptpl.common.util.EncryptionTool;

/**
 * MyBatis Executor 인터셉터
 * 1. MapperProxy에서 파라미터 바인딩 예외가 발생하는 것을 막기 위해 VO 객체의 필드를 파라미터 Map에 최상위 키로 추가한다.
 * 2. 모든 쿼리에 현재 로그인 사용자 정보를 자동으로 추가한다.
 */
@Slf4j
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class MybatisParameterInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object[] args = invocation.getArgs();
            
            // MappedStatement는 첫 번째 인자에 위치한다.
            MappedStatement mappedStatement = (MappedStatement) args[0];
            
            // 서브 쿼리(내부 쿼리) 파라미터 조작 대상에서 제외하여 안정성을 높인다.
            boolean isInnerStatement = mappedStatement.getId().contains("!");
            if (isInnerStatement) {
                return invocation.proceed();
            }

            Object parameterObject = args[1];

            // 파라미터 객체 타입에 따른 처리
            if (parameterObject instanceof MapperMethod.ParamMap) {
                // ParamMap인 경우
                @SuppressWarnings("unchecked")
                Map<String, Object> paramMap = (Map<String, Object>) parameterObject;
                
                // 1. VO 필드를 파라미터 맵에 추가 (기존 기능)
                processParamMap(paramMap);
                
                // 2. @Encrypt 어노테이션이 있는 파라미터 암호화
                encryptParameters(paramMap);
                
                // 3. 세션 사용자 정보를 파라미터 맵에 추가 (신규 기능)
                addSessionUserToParamMap(paramMap, mappedStatement);
                
            } else if (parameterObject != null && isBusinessObject(parameterObject)) {
                // VO 객체인 경우 - 새로운 ParamMap을 생성하여 처리
                try {
                    // 새로운 ParamMap 생성
                    Map<String, Object> newParamMap = new MapperMethod.ParamMap<>();
                    newParamMap.put("param1", parameterObject);
                    newParamMap.put("arg0", parameterObject);
                    
                    // VO 필드를 맵에 추가
                    flattenVoFieldsIntoMap(parameterObject, newParamMap);
                    
                    // @Encrypt 어노테이션이 있는 파라미터 암호화
                    encryptParameters(newParamMap);
                    
                    // 세션 사용자 정보 추가
                    addSessionUserToParamMap(newParamMap, mappedStatement);
                    
                    // 원래 파라미터를 새로운 ParamMap으로 교체
                    args[1] = newParamMap;
                    
                } catch (SecurityException e) {
                    // 보안/접근 권한 오류
                    log.error("VO 객체 처리 중 보안/접근 권한 오류 - Mapper: {}, 객체 타입: {}", 
                        mappedStatement.getId(), parameterObject.getClass().getSimpleName(), e);
                    // Fallback: 세션 사용자 정보만 추가
                    createFallbackParamMap(parameterObject, mappedStatement, args);
                    
                } catch (ClassCastException | IllegalArgumentException | NullPointerException e) {
                    // 데이터 타입/입력값 오류
                    log.error("VO 객체 처리 중 데이터 타입/입력값 오류 - Mapper: {}, 객체 타입: {}", 
                        mappedStatement.getId(), parameterObject.getClass().getSimpleName(), e);
                    // Fallback: 세션 사용자 정보만 추가
                    createFallbackParamMap(parameterObject, mappedStatement, args);
                    
                } catch (Exception e) {
                    // 예상치 못한 시스템 오류
                    log.error("VO 객체 처리 중 예상치 못한 오류 - Mapper: {}, 객체 타입: {}", 
                        mappedStatement.getId(), parameterObject.getClass().getSimpleName(), e);
                    // Fallback: 세션 사용자 정보만 추가
                    createFallbackParamMap(parameterObject, mappedStatement, args);
                }
            }

            return invocation.proceed();
            
        } catch (ClassCastException | ArrayIndexOutOfBoundsException | NullPointerException | IllegalArgumentException e) {
            // 데이터 타입/구조 오류 (심각한 문제이므로 전파)
            log.error("MyBatis 인터셉터 데이터 타입/구조 오류 - 메서드: {}, 오류: {}", 
                invocation.getMethod().getName(), e.getClass().getSimpleName(), e);
            throw e;
            
        } catch (SecurityException e) {
            // 보안/접근 권한 오류 (심각한 문제이므로 전파)
            log.error("MyBatis 인터셉터 보안/접근 권한 오류 - 메서드: {}, 오류: {}", 
                invocation.getMethod().getName(), e.getClass().getSimpleName(), e);
            throw e;
            
        } catch (Exception e) {
            // 예상치 못한 시스템 오류 (쿼리 실행은 시도)
            log.error("MyBatis 인터셉터 처리 중 예상치 못한 오류 - 메서드: {}, 오류: {}", 
                invocation.getMethod().getName(), e.getClass().getSimpleName(), e);
            // 예상치 못한 오류 발생 시에도 원본 쿼리는 실행되도록 함
            return invocation.proceed();
        }
    }

    /**
     * VO 처리 실패 시 Fallback ParamMap 생성
     * @param parameterObject 파라미터 객체
     * @param mappedStatement MyBatis 매핑 구문
     * @param args 인자 배열
     */
    private void createFallbackParamMap(Object parameterObject, MappedStatement mappedStatement, Object[] args) {
        try {
            Map<String, Object> fallbackParamMap = new MapperMethod.ParamMap<>();
            fallbackParamMap.put("param1", parameterObject);
            fallbackParamMap.put("arg0", parameterObject);
            addSessionUserToParamMap(fallbackParamMap, mappedStatement);
            args[1] = fallbackParamMap;
            
        } catch (NullPointerException | IllegalArgumentException | ClassCastException | ArrayIndexOutOfBoundsException e) {
            // 데이터 타입/입력값 오류
            log.error("Fallback ParamMap 생성 실패 - 데이터 타입/입력값 오류: Mapper={}, 오류={}", 
                mappedStatement.getId(), e.getClass().getSimpleName());
            log.debug("오류 상세: {}", e.getMessage());
            // 원본 파라미터 유지 (args 변경 없음)
            
        } catch (Exception e) {
            // 예상치 못한 시스템 오류
            log.error("Fallback ParamMap 생성 실패 - 예상치 못한 오류: Mapper={}, 오류={}", 
                mappedStatement.getId(), e.getClass().getSimpleName(), e);
            // 원본 파라미터 유지 (args 변경 없음)
        }
    }

    /**
     * 파라미터 맵을 처리하여 VO 객체의 필드를 맵에 추가한다.
     * @param paramMap MyBatis가 생성한 파라미터 맵
     */
    private void processParamMap(Map<String, Object> paramMap) {
        Object voParameter = findVoParameter(paramMap);

        if (voParameter != null) {
            flattenVoFieldsIntoMap(voParameter, paramMap);
        }
    }

    /**
     * 파라미터 맵에서 비즈니스 로직을 담은 VO 객체를 찾는다.
     * @param paramMap 파라미터 맵
     * @return VO 객체 (없으면 null)
     */
    private Object findVoParameter(Map<String, Object> paramMap) {
        for (Object value : paramMap.values()) {
            if (value != null && isBusinessObject(value)) {
                return value;
            }
        }
        return null;
    }
    
    /**
     * 해당 객체가 Pageable이나 Java 기본 타입이 아닌, 비즈니스 객체(VO, DTO 등)인지 판별한다.
     */
    private boolean isBusinessObject(Object obj) {
        if (obj instanceof Pageable) return false;
        
        String className = obj.getClass().getName();
        return !className.startsWith("java.") && !obj.getClass().isPrimitive();
    }

    /**
     * VO 객체의 필드를 파라미터 맵에 최상위 키로 추가한다.
     * @param voObject VO 객체
     * @param paramMap 파라미터 맵
     */
    private void flattenVoFieldsIntoMap(Object voObject, Map<String, Object> paramMap) {
        try {
            Class<?> voClass = voObject.getClass();
            Field[] fields = voClass.getDeclaredFields();

            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    // 필드 이름으로 이미 키가 존재하지 않을 경우에만 추가한다 (putIfAbsent).
                    // 이는 "param1", "arg0" 등 MyBatis가 자동 생성한 키와의 충돌을 방지한다.
                    paramMap.putIfAbsent(field.getName(), field.get(voObject));
                } catch (IllegalAccessException e) {
                    log.debug("필드 접근 실패 - 클래스: {}, 필드: {}, 접근 불가능한 필드로 스킵", 
                        voClass.getSimpleName(), field.getName());
                } catch (SecurityException e) {
                    log.warn("필드 보안 정책으로 접근 실패 - 클래스: {}, 필드: {}", 
                        voClass.getSimpleName(), field.getName());
                } catch (Exception e) {
                    log.warn("필드 처리 중 예상치 못한 오류 - 클래스: {}, 필드: {}, 오류: {}", 
                        voClass.getSimpleName(), field.getName(), e.getMessage());
                }
            }
        } catch (SecurityException e) {
            log.error("VO 클래스 필드 정보 접근 실패 - 클래스: {}, 보안 정책 오류: {}", 
                voObject.getClass().getSimpleName(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("VO 객체 필드 변환 중 잘못된 인자 - 클래스: {}, 오류: {}", 
                voObject.getClass().getSimpleName(), e.getMessage());
        } catch (NullPointerException e) {
            log.error("VO 객체 또는 파라미터 맵이 null - voObject: {}, paramMap: {}", 
                voObject, paramMap != null ? "not null" : "null");
        } catch (Exception e) {
            log.error("VO 객체 필드 변환 중 예상치 못한 오류 - 클래스: {}, 오류 타입: {}, 메시지: {}", 
                voObject.getClass().getSimpleName(), e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * @Encrypt 어노테이션이 있는 파라미터를 암호화합니다.
     * @param paramMap 파라미터 맵
     */
    private void encryptParameters(Map<String, Object> paramMap) {
        if (paramMap == null) {
            return;
        }
        
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            Object value = entry.getValue();
            
            // 값이 VO 객체인 경우 해당 객체의 필드를 확인
            if (value != null && isBusinessObject(value)) {
                encryptObjectFields(value, paramMap);
            }
        }
    }
    
    /**
     * VO 객체의 @Encrypt 어노테이션이 있는 필드를 암호화합니다.
     * @param voObject VO 객체
     * @param paramMap 파라미터 맵 (암호화된 값을 업데이트하기 위해 사용)
     */
    private void encryptObjectFields(Object voObject, Map<String, Object> paramMap) {
        if (voObject == null) {
            return;
        }
        
        try {
            Class<?> clazz = voObject.getClass();
            Field[] fields = clazz.getDeclaredFields();
            
            for (Field field : fields) {
                Encrypt encrypt = AnnotationUtils.findAnnotation(field, Encrypt.class);
                if (encrypt == null) {
                    continue;
                }
                
                if (!String.class.equals(field.getType())) {
                    log.warn("@Encrypt는 String 타입 필드에만 적용 가능합니다. 필드: {}.{}", 
                            clazz.getSimpleName(), field.getName());
                    continue;
                }
                
                try {
                    field.setAccessible(true);
                    Object value = field.get(voObject);
                    
                    if (value instanceof String && value != null && !((String) value).isEmpty()) {
                        try {
                            String encrypted = EncryptionTool.encryptField((String) value);
                            field.set(voObject, encrypted);
                            
                            // paramMap에 해당 필드가 이미 추가되어 있다면 암호화된 값으로 업데이트
                            if (paramMap != null && paramMap.containsKey(field.getName())) {
                                paramMap.put(field.getName(), encrypted);
                            }
                        } catch (IllegalArgumentException e) {
                            log.warn("암호화할 값이 유효하지 않음: {}.{}, 원본 값 유지", 
                                    clazz.getSimpleName(), field.getName());
                        } catch (SecurityException e) {
                            log.error("보안 정책으로 인한 암호화 실패: {}.{}", 
                                    clazz.getSimpleName(), field.getName(), e);
                        } catch (RuntimeException e) {
                            log.error("암호화 처리 실패: {}.{}, 원본 값 유지. 오류: {}", 
                                    clazz.getSimpleName(), field.getName(), e.getMessage());
                        } catch (Exception e) {
                            log.error("예상치 못한 암호화 오류: {}.{}, 원본 값 유지", 
                                    clazz.getSimpleName(), field.getName(), e);
                        }
                    }
                } catch (IllegalAccessException e) {
                    log.warn("필드 접근 실패: {}.{}", clazz.getSimpleName(), field.getName(), e);
                } catch (Exception e) {
                    log.warn("필드 암호화 처리 중 오류: {}.{}, 원본 값 유지. 오류: {}", 
                            clazz.getSimpleName(), field.getName(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("VO 객체 암호화 처리 중 오류: {}, 원본 값 유지. 오류: {}", 
                    voObject.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 세션 사용자 정보를 파라미터 맵에 추가한다.
     * @param paramMap 파라미터 맵
     * @param mappedStatement MyBatis 매핑 구문
     */
    private void addSessionUserToParamMap(Map<String, Object> paramMap, MappedStatement mappedStatement) {
        try {
            // 현재 로그인 사용자 정보 가져오기
            SecUser currentUser = SecurityUtils.getCurrentUser();
            
            if (currentUser != null) {
                try {
                    // 1. 세션 사용자 정보를 Map 형태로 추가 (sessionUser.필드명 접근용)
                    Map<String, Object> sessionUserMap = new HashMap<>();
                    sessionUserMap.put("userId", currentUser.getUserId());
                    sessionUserMap.put("ognzCd", currentUser.getOgnzCd());
                    sessionUserMap.put("authrtCd", currentUser.getAuthrtCd());
                    sessionUserMap.put("ognzInqRangCd", currentUser.getOgnzInqRangCd());
                    paramMap.put("sessionUser", sessionUserMap);  // putIfAbsent 대신 put 사용
                } catch (NullPointerException e) {
                    log.warn("사용자 정보 필드 접근 중 null 값 발견 - Mapper: {}, 기본값으로 설정", 
                        mappedStatement.getId());
                    setAnonymousUser(paramMap);
                } catch (Exception e) {
                    log.error("사용자 정보 맵 생성 중 오류 - Mapper: {}, 오류: {}", 
                        mappedStatement.getId(), e.getMessage());
                    setAnonymousUser(paramMap);
                }
            } else {
                // 익명 사용자인 경우 기본값 설정
                setAnonymousUser(paramMap);
            }
        } catch (SecurityException e) {
            log.error("보안 컨텍스트 접근 실패 - Mapper: {}, 보안 오류: {}", 
                mappedStatement.getId(), e.getMessage());
            setErrorUser(paramMap);
        } catch (IllegalStateException e) {
            log.error("보안 컨텍스트 상태 오류 - Mapper: {}, 상태 오류: {}", 
                mappedStatement.getId(), e.getMessage());
            setErrorUser(paramMap);
        } catch (ClassCastException e) {
            log.error("사용자 정보 타입 캐스팅 오류 - Mapper: {}, 타입 오류: {}", 
                mappedStatement.getId(), e.getMessage());
            setErrorUser(paramMap);
        } catch (OutOfMemoryError e) {
            log.error("세션 사용자 정보 처리 중 메모리 부족 - Mapper: {}", mappedStatement.getId());
            setErrorUser(paramMap);
        } catch (Exception e) {
            log.error("세션 사용자 정보 처리 중 예상치 못한 오류 - Mapper: {}, 오류 타입: {}, 메시지: {}", 
                mappedStatement.getId(), e.getClass().getSimpleName(), e.getMessage());
            setErrorUser(paramMap);
        }
    }
    
    /**
     * 익명 사용자 정보를 설정한다.
     * @param paramMap 파라미터 맵
     */
    private void setAnonymousUser(Map<String, Object> paramMap) {
        Map<String, Object> sessionUserMap = new HashMap<>();
        sessionUserMap.put("userId", "ANONYMOUS");
        sessionUserMap.put("ognzCd", "");
        sessionUserMap.put("authrtCd", "");
        sessionUserMap.put("ognzInqRangCd", "03");
        paramMap.put("sessionUser", sessionUserMap);
    }
    
    /**
     * 오류 상황에서 기본 사용자 정보를 설정한다.
     * @param paramMap 파라미터 맵
     */
    private void setErrorUser(Map<String, Object> paramMap) {
        try {
            Map<String, Object> sessionUserMap = new HashMap<>();
            sessionUserMap.put("userId", "ERROR");
            sessionUserMap.put("ognzCd", "");
            sessionUserMap.put("authrtCd", "");
            sessionUserMap.put("ognzInqRangCd", "03");
            paramMap.put("sessionUser", sessionUserMap);
            
        } catch (NullPointerException | IllegalArgumentException e) {
            // 입력값 오류 (paramMap이 null이거나 잘못된 인자)
            log.error("기본 사용자 정보 설정 실패 - 입력값 오류: {}", e.getMessage());
            // paramMap이 null이면 아무 것도 할 수 없음
            
        } catch (UnsupportedOperationException | IllegalStateException e) {
            // 맵 상태 오류 (불변 맵이거나 수정 불가능한 상태)
            log.error("기본 사용자 정보 설정 실패 - 맵 상태 오류: {}", e.getMessage());
            // 맵이 수정 불가능하면 아무 것도 할 수 없음
            
        } catch (OutOfMemoryError e) {
            // 메모리 부족 오류
            log.error("기본 사용자 정보 설정 실패 - 메모리 부족");
            // 메모리 부족 시에는 추가 작업 불가
            throw e; // 메모리 오류는 상위로 전파하여 시스템이 대응하도록 함
            
        } catch (Exception e) {
            // 예상치 못한 시스템 오류
            log.error("기본 사용자 정보 설정 실패 - 예상치 못한 오류: {}", e.getMessage(), e);
            // 최후의 수단으로 빈 맵이라도 설정 시도
            try {
                paramMap.put("sessionUser", new HashMap<>());
            } catch (Exception fallbackException) {
                log.error("빈 맵 설정마저 실패: {}", fallbackException.getMessage());
            }
        }
    }
} 