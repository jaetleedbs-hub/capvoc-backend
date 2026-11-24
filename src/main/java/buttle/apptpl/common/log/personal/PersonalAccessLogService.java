package buttle.apptpl.common.log.personal;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.config.EnvConfig;
import micoai.corebase.pii.Pii;
import micoai.corebase.pii.PiiMasker;
import micoai.corebase.util.CoreUtils.aes256;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PersonalAccessLogService {

    /** 감사로그 DB 저장 실패 시 유실을 남기는 전용 채널. logback 에서 별도 파일로 분리 권장. */
    private static final Logger AUDIT_FALLBACK = LoggerFactory.getLogger("PERSONAL_ACCESS_AUDIT_FALLBACK");

    private final LogtPersonalAccessLogRepo logtPersonalAccessLogRepo;
    private final EnvConfig envConfig;

    @Async("taskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLogAsync(List<LogtPersonalAccessLogEntity> logEntities) {
        if (logEntities == null || logEntities.isEmpty()) {
            return;
        }
        
        try {
            logtPersonalAccessLogRepo.saveAll(logEntities);
            log.debug("개인정보 접근 로그 {} 건 저장 완료", logEntities.size());

        } catch (Exception e) {
            // [N-12 / W3-3] 감사로그 무결성 — 저장 실패를 <삼키지 않는다>.
            // 이 메서드는 응답 반환 뒤 비동기로 도는 구조라 요청을 되돌릴 수는 없다. 대신 유실을
            // 막기 위해, 저장하려던 항목을 전용 감사-실패 채널에 <복구 가능한 형태>로 동기 기록한다
            // (계획서 W3-3 의 "최소 동기 기록"). 엔티티는 이미 마스킹·암호화된 상태라 이 폴백
            // 기록에 평문 개인정보는 없다. 요청-저장 원자성을 보장하는 outbox 는 별도 트랙(DDL).
            recordAuditFallback(logEntities, e);
        }
    }

    /**
     * 개인정보 접근 로그 DB 저장 실패 시 유실 방지 폴백.
     * 저장하려던 항목을 전용 로거({@code PERSONAL_ACCESS_AUDIT_FALLBACK})로 남겨,
     * 운영이 감사 유실을 탐지하고 사후 복구할 수 있게 한다. logback 에서 이 로거를
     * 별도 파일(WORM)로 분리하면 추적성이 높아진다.
     */
    private void recordAuditFallback(List<LogtPersonalAccessLogEntity> logEntities, Exception cause) {
        log.error("개인정보 접근 로그 DB 저장 실패 — 감사 폴백 기록 {}건: {}",
            logEntities.size(), cause.getMessage(), cause);
        for (LogtPersonalAccessLogEntity en : logEntities) {
            AUDIT_FALLBACK.error(
                "AUDIT_LOSS logId={} hstNo={} grp={} rowNo={} fld={} mskTyp={} msk={} enc={}",
                en.getLogId(), en.getHstNo(), en.getGrpNm(), en.getRowNo(),
                en.getFldNm(), en.getMskTyp(), en.getMskVal(), en.getEncVal());
        }
    }

    // [N-18] 부모키를 log_sid(Long)에서 표준 log_id(String)로 전환. rgtrId(현재 사용자 lgn_id)도 함께 기록.
    public void processAndSavePersonalLogs(Object responseData, String logId, String rgtrId) {
        List<LogtPersonalAccessLogEntity> personalLogs = new ArrayList<>();
        AtomicInteger hstNo = new AtomicInteger(0);
        parseObject(responseData, logId, rgtrId, hstNo, personalLogs, "");
        saveLogAsync(personalLogs);
    }

    private void parseObject(Object obj, String logId, String rgtrId, AtomicInteger hstNo, List<LogtPersonalAccessLogEntity> personalLogs, String parentName) {
        if (obj == null) {
            return;
        }

        if (obj instanceof Collection) {
            int rowNo = 0;
            for (Object item : (Collection<?>) obj) {
                parseFields(item, logId, rgtrId, hstNo, personalLogs, parentName, rowNo++);
            }
        } else {
            parseFields(obj, logId, rgtrId, hstNo, personalLogs, parentName, 0);
        }
    }

    private void parseFields(Object obj, String logId, String rgtrId, AtomicInteger hstNo, List<LogtPersonalAccessLogEntity> personalLogs, String parentName, int rowNo) {
        if (obj == null) {
            return;
        }
        
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                // Java 모듈 시스템 호환성을 위한 안전한 접근
                if (!isAccessibleField(field)) {
                    continue;
                }
                
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value == null || StringUtils.isEmpty(value.toString())) {
                    continue;
                }

                String grpNm = parentName.isEmpty() ? obj.getClass().getSimpleName().toLowerCase() : parentName;

                if (field.isAnnotationPresent(Pii.class)) {
                    Pii pii = field.getAnnotation(Pii.class);
                    String originalValue = value.toString();
                    String maskedValue = PiiMasker.mask(originalValue, pii.value());

                    // [enc_val 표준화] 난독화 대신 AES-256-GCM(connect·reveal 과 동일 표준).
                    String encryptedValue = aes256.encryptGcm(originalValue, envConfig.getAesKey());

                    personalLogs.add(LogtPersonalAccessLogEntity.builder()
                            .logId(logId)
                            .rgtrId(rgtrId)
                            .regDt(java.time.LocalDateTime.now())
                            .hstNo(hstNo.getAndIncrement())
                            .grpNm(grpNm)
                            .rowNo(rowNo)
                            .fldNm(field.getName())
                            .encVal(encryptedValue)
                            .mskVal(maskedValue)
                            .mskTyp(pii.value().name())
                            .build());

                } else if (value instanceof Collection) {
                    int innerRowNo = 0;
                    for (Object item : (Collection<?>) value) {
                        parseFields(item, logId, rgtrId, hstNo, personalLogs, field.getName(), innerRowNo++);
                    }
                } else if (isCustomClass(field.getType())) {
                     parseFields(value, logId, rgtrId, hstNo, personalLogs, field.getName(), rowNo);
                }

            } catch (IllegalAccessException e) {
                log.error("Field access error: {}", e.getMessage(), e);
            } catch (InaccessibleObjectException e) {
                log.warn("Cannot access field {} in class {} due to module restrictions", 
                    field.getName(), obj.getClass().getName());
            } catch (Exception e) {
                log.error("Unexpected error accessing field {}: {}", field.getName(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * 필드가 안전하게 접근 가능한지 확인
     */
    private boolean isAccessibleField(Field field) {
        String className = field.getDeclaringClass().getName();
        
        // Java 내부 클래스들은 접근하지 않음
        if (className.startsWith("java.") || 
            className.startsWith("javax.") || 
            className.startsWith("sun.") ||
            className.startsWith("com.sun.") ||
            className.startsWith("jdk.")) {
            return false;
        }
        
        // 프리미티브 타입은 접근하지 않음
        if (field.getType().isPrimitive()) {
            return false;
        }
        
        // // String, Number 등 기본 래퍼 클래스는 접근하지 않음
        // String fieldTypeName = field.getType().getName();
        // if (fieldTypeName.equals("java.lang.String") ||
        //     fieldTypeName.equals("java.lang.Integer") ||
        //     fieldTypeName.equals("java.lang.Long") ||
        //     fieldTypeName.equals("java.lang.Double") ||
        //     fieldTypeName.equals("java.lang.Float") ||
        //     fieldTypeName.equals("java.lang.Boolean") ||
        //     fieldTypeName.equals("java.lang.Character") ||
        //     fieldTypeName.equals("java.lang.Byte") ||
        //     fieldTypeName.equals("java.lang.Short")) {
        //     return false;
        // }
        
        return true;
    }
    
    /**
     * 커스텀 클래스인지 확인 (재귀적으로 파싱해야 하는 클래스)
     */
    private boolean isCustomClass(Class<?> clazz) {
        String className = clazz.getName();
        
        // 애플리케이션 패키지의 클래스만 재귀적으로 파싱
        return className.startsWith("buttle.apptpl."); // 필요시 다른 패키지 추가
    }
}
