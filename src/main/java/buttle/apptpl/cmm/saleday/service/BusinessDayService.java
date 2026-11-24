package buttle.apptpl.cmm.saleday.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.cmm.saleday.dto.SYS5030001Dto;
import buttle.apptpl.cmm.saleday.dto.SYS5030002Dto;
import buttle.apptpl.cmm.saleday.dto.SYS5030003Dto;
import buttle.apptpl.cmm.saleday.mapper.BusinessDayMapper;
import buttle.apptpl.cmm.saleday.vo.SYS5030001Vo;
import buttle.apptpl.cmm.saleday.vo.SYS5030002Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptSaleDayEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptSaleDayPkey;
import buttle.apptpl.common.model.cmpt.repository.CmptSaleDayRepo;
import buttle.apptpl.common.util.ModelConveter;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BusinessDayService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int BATCH_SIZE = 500; // 배치 처리 크기

    private final BusinessDayMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptSaleDayRepo cmptSaleDayRepo;

    /**
     * 영업일 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<SYS5030002Dto> selectBusinessDayList(SYS5030001Dto dto, Pageable pageable) {
        SYS5030001Vo vo = modelConveter.map(dto, SYS5030001Vo.class);
        Page<SYS5030002Vo> resultPage = mapper.selectBusinessDayList(vo, pageable);
        return resultPage.map(voItem -> modelConveter.map(voItem, SYS5030002Dto.class));
    }

    /**
     * 영업일 생성 (기간별 배치 처리)
     */
    public SYS5030003Dto createBusinessDay(SYS5030001Dto dto) {

        try {
            // 입력값 검증
            validateBusinessDayInput(dto);
            
            // 영업일 배열 생성
            List<SYS5030002Dto> businessDayList = generateBusinessDayList(dto);
            
            if (businessDayList.isEmpty()) {
                return SYS5030003Dto.builder()
                    .result(false)
                    .message("생성할 영업일이 없습니다.")
                    .build();
            }
            
            // 기존 데이터 중복 검사 (강제 생성이 아닌 경우)
            if (!"Y".equals(dto.getForceCreateYn())) {
                List<SYS5030002Dto> duplicates = checkDuplicateBusinessDays(businessDayList);
                if (!duplicates.isEmpty()) {
                    return SYS5030003Dto.builder()
                        .result(false)
                        .message(String.format("이미 존재하는 영업일이 %d건 있습니다. 강제생성을 선택해주세요.", duplicates.size()))
                        .build();
                }
            }
            
            // 배치 처리로 영업일 저장
            int savedCount = batchInsertBusinessDays(businessDayList);
            
            log.info("영업일 생성 완료 - 요청기간: {} ~ {}, 조직: {}, 생성건수: {}", 
                dto.getFromSaleDay(), dto.getToSaleDay(), dto.getOgnzCd(), savedCount);
            
            return SYS5030003Dto.builder()
                .result(true)
                .message(String.format("%d건의 영업일이 생성되었습니다.", savedCount))
                .build();
                
        } catch (InvalidationException e) {
            log.error("영업일 생성 검증 오류: {}", e.getMessage());
            return SYS5030003Dto.builder()
                .result(false)
                .message(e.getMessage())
                .build();
        } catch (Exception e) {
            log.error("영업일 생성 중 오류 발생", e);
            return SYS5030003Dto.builder()
                .result(false)
                .message("영업일 생성 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }
    
    /**
     * 영업일 입력값 검증
     */
    private void validateBusinessDayInput(SYS5030001Dto dto) {
        if (!StringUtils.hasText(dto.getFromSaleDay())) {
            throw new InvalidationException("시작 영업일을 입력하세요.");
        }
        if (!StringUtils.hasText(dto.getToSaleDay())) {
            throw new InvalidationException("종료 영업일을 입력하세요.");
        }
        if (!StringUtils.hasText(dto.getOgnzCd())) {
            throw new InvalidationException("조직코드를 입력하세요.");
        }
        
        // 날짜 형식 검증
        try {
            LocalDate fromDate = LocalDate.parse(dto.getFromSaleDay(), DATE_FORMATTER);
            LocalDate toDate = LocalDate.parse(dto.getToSaleDay(), DATE_FORMATTER);
            
            if (fromDate.isAfter(toDate)) {
                throw new InvalidationException("시작일이 종료일보다 늦을 수 없습니다.");
            }
            
            // 최대 3년 제한
            if (fromDate.plusYears(3).isBefore(toDate)) {
                throw new InvalidationException("영업일 생성 기간은 최대 3년까지 가능합니다.");
            }
            
        } catch (DateTimeParseException e) {
            throw new InvalidationException("날짜 형식이 올바르지 않습니다. (YYYYMMDD 형식으로 입력하세요)");
        }
    }
    
    /**
     * 영업일 배열 생성 (시작일~종료일)
     */
    private List<SYS5030002Dto> generateBusinessDayList(SYS5030001Dto dto) {
        List<SYS5030002Dto> businessDayList = new ArrayList<>();
        
        LocalDate fromDate = LocalDate.parse(dto.getFromSaleDay(), DATE_FORMATTER);
        LocalDate toDate = LocalDate.parse(dto.getToSaleDay(), DATE_FORMATTER);
        
        LocalDate currentDate = fromDate;
        while (!currentDate.isAfter(toDate)) {
            SYS5030002Dto businessDay = SYS5030002Dto.builder()
                .saleDay(currentDate.format(DATE_FORMATTER))
                .ognzCd(dto.getOgnzCd())
                .hldySeCd(dto.getHldySeCd() != null ? dto.getHldySeCd() : determineHolidayType(currentDate))
                .wdaySeCd(determineWeekdayType(currentDate))
                .rmrk(generateRemarks(currentDate, dto))
                .build();
                
            businessDayList.add(businessDay);
            currentDate = currentDate.plusDays(1);
        }
        
        return businessDayList;
    }
    
    /**
     * 휴일 구분 자동 판단
     */
    private String determineHolidayType(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        
        // 토요일, 일요일은 휴일로 설정
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return "WEEKEND"; // 주말
        }
        
        // 고정 공휴일 체크
        if (isFixedHoliday(date)) {
            return "HOLIDAY"; // 공휴일
        }
        
        // TODO: 동적 공휴일 판단 로직 추가 (설날, 추석 등 음력 기반)
        // TODO: 공휴일 테이블 조회 추가
        
        return "NORMAL"; // 일반 영업일
    }
    
    /**
     * 고정 공휴일 체크
     */
    private boolean isFixedHoliday(LocalDate date) {
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        
        // 신정
        if (month == 1 && day == 1) return true;
        // 삼일절
        if (month == 3 && day == 1) return true;
        // 어린이날
        if (month == 5 && day == 5) return true;
        // 현충일
        if (month == 6 && day == 6) return true;
        // 광복절
        if (month == 8 && day == 15) return true;
        // 개천절
        if (month == 10 && day == 3) return true;
        // 한글날
        if (month == 10 && day == 9) return true;
        // 크리스마스
        if (month == 12 && day == 25) return true;
        
        return false;
    }
    
    /**
     * 요일 구분 설정
     */
    private String determineWeekdayType(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.name(); // MONDAY, TUESDAY, ..., SUNDAY
    }
    
    /**
     * 비고 생성
     */
    private String generateRemarks(LocalDate date, SYS5030001Dto dto) {
        StringBuilder remarks = new StringBuilder();
        
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            remarks.append("주말");
        }
        
        // 월초, 월말 표시
        if (date.getDayOfMonth() == 1) {
            if (remarks.length() > 0) remarks.append(", ");
            remarks.append("월초");
        } else if (date.getDayOfMonth() == date.lengthOfMonth()) {
            if (remarks.length() > 0) remarks.append(", ");
            remarks.append("월말");
        }
        
        return remarks.toString();
    }
    
    /**
     * 중복 영업일 검사 (배치 최적화)
     */
    private List<SYS5030002Dto> checkDuplicateBusinessDays(List<SYS5030002Dto> businessDayList) {
        List<SYS5030002Dto> duplicates = new ArrayList<>();
        
        // 배치로 중복 검사 (성능 향상)
        List<CmptSaleDayPkey> pkeys = businessDayList.stream()
            .map(dto -> new CmptSaleDayPkey(dto.getSaleDay(), dto.getOgnzCd()))
            .collect(Collectors.toList());
            
        List<CmptSaleDayEntity> existingEntities = cmptSaleDayRepo.findAllById(pkeys);
        
        // 기존 데이터와 비교
        Map<CmptSaleDayPkey, Boolean> existingMap = existingEntities.stream()
            .collect(Collectors.toMap(CmptSaleDayEntity::getId, entity -> true));
            
        for (SYS5030002Dto businessDay : businessDayList) {
            CmptSaleDayPkey pkey = new CmptSaleDayPkey(businessDay.getSaleDay(), businessDay.getOgnzCd());
            if (existingMap.containsKey(pkey)) {
                duplicates.add(businessDay);
            }
        }
        
        return duplicates;
    }
    
    /**
     * 영업일 배치 저장 (성능 최적화)
     */
    private int batchInsertBusinessDays(List<SYS5030002Dto> businessDayList) {
        List<CmptSaleDayEntity> entitiesToSave = new ArrayList<>();
        int totalSaved = 0;
        
        // 기존 데이터 조회 (배치 최적화)
        Map<CmptSaleDayPkey, CmptSaleDayEntity> existingEntities = getExistingEntities(businessDayList);
        
        for (SYS5030002Dto dto : businessDayList) {
            CmptSaleDayPkey pkey = new CmptSaleDayPkey(dto.getSaleDay(), dto.getOgnzCd());
            
            CmptSaleDayEntity entity;
            CmptSaleDayEntity existingEntity = existingEntities.get(pkey);
            
            if (existingEntity != null) {
                // 수정: 기존 엔티티 업데이트
                entity = existingEntity;
                BeanUtils.copyProperties(dto, entity, "saleDay", "ognzCd");
            } else {
                // 신규: 새 엔티티 생성
                entity = new CmptSaleDayEntity();
                entity.setId(pkey);
                BeanUtils.copyProperties(dto, entity, "saleDay", "ognzCd");
            }
            
            entitiesToSave.add(entity);
            
            // 배치 크기만큼 모이면 저장
            if (entitiesToSave.size() >= BATCH_SIZE) {
                totalSaved += saveBatch(entitiesToSave);
                entitiesToSave.clear();
            }
        }
        
        // 나머지 데이터 저장
        if (!entitiesToSave.isEmpty()) {
            totalSaved += saveBatch(entitiesToSave);
        }
        
        return totalSaved;
    }
    
    /**
     * 기존 엔티티 조회 (배치 최적화)
     */
    private Map<CmptSaleDayPkey, CmptSaleDayEntity> getExistingEntities(List<SYS5030002Dto> businessDayList) {
        List<CmptSaleDayPkey> pkeys = businessDayList.stream()
            .map(dto -> new CmptSaleDayPkey(dto.getSaleDay(), dto.getOgnzCd()))
            .collect(Collectors.toList());
            
        return cmptSaleDayRepo.findAllById(pkeys)
            .stream()
            .collect(Collectors.toMap(CmptSaleDayEntity::getId, entity -> entity));
    }
    
    /**
     * 배치 저장 실행
     */
    private int saveBatch(List<CmptSaleDayEntity> entities) {
        List<CmptSaleDayEntity> savedEntities = cmptSaleDayRepo.saveAll(entities);
        cmptSaleDayRepo.flush(); // 즉시 DB 반영
        return savedEntities.size();

    }


    /**
     * 영업일 저장
     */
    public Integer updateBusinessDayList(List<SYS5030002Dto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new InvalidationException("저장할 영업일 데이터가 없습니다.");
        }

        for (SYS5030002Dto dto : dtoList) {
            // 필수 필드 검증
            if (ObjectUtils.isEmpty(dto.getSaleDay())) {
                throw new InvalidationException("영업일을 입력하세요.");
            }
            if (ObjectUtils.isEmpty(dto.getOgnzCd())) {
                throw new InvalidationException("조직코드를 입력하세요.");
            }

            // 복합키 생성
            CmptSaleDayPkey pkey = new CmptSaleDayPkey(dto.getSaleDay(), dto.getOgnzCd());

            // 기존 데이터 확인
            CmptSaleDayEntity existingEntity = cmptSaleDayRepo.findById(pkey).orElse(null);

            CmptSaleDayEntity entity;
            if (existingEntity != null) {
                // 수정: 기존 엔티티 업데이트 (복합키와 감사필드는 제외)
                entity = existingEntity;
                BeanUtils.copyProperties(dto, entity, "saleDay", "ognzCd");
            } else {
                // 신규 등록: 새 엔티티 생성
                entity = new CmptSaleDayEntity();
                entity.setId(pkey);  // 복합키 설정
                BeanUtils.copyProperties(dto, entity, "saleDay", "ognzCd");
            }

            cmptSaleDayRepo.save(entity);
        }

        return dtoList.size();
    }
    
    /**
     * 대량 영업일 삭제 (기간별)
     */
    public SYS5030003Dto deleteBusinessDaysByPeriod(SYS5030001Dto dto) {
        // 입력값 검증
        validateBusinessDayInput(dto);
        
        LocalDate fromDate = LocalDate.parse(dto.getFromSaleDay(), DATE_FORMATTER);
        LocalDate toDate = LocalDate.parse(dto.getToSaleDay(), DATE_FORMATTER);
        
        // 삭제할 영업일 목록 생성
        List<CmptSaleDayPkey> pkeysToDelete = new ArrayList<>();
        LocalDate currentDate = fromDate;
        
        while (!currentDate.isAfter(toDate)) {
            pkeysToDelete.add(new CmptSaleDayPkey(currentDate.format(DATE_FORMATTER), dto.getOgnzCd()));
            currentDate = currentDate.plusDays(1);
        }
        
        // 배치 삭제
        int deletedCount = batchDeleteBusinessDays(pkeysToDelete);
        
        log.info("영업일 삭제 완료 - 요청기간: {} ~ {}, 조직: {}, 삭제건수: {}", 
            dto.getFromSaleDay(), dto.getToSaleDay(), dto.getOgnzCd(), deletedCount);
        
        return SYS5030003Dto.builder()
            .result(true)
            .message(String.format("%d건의 영업일이 삭제되었습니다.", deletedCount))
            .build();
                

    }
    
    /**
     * 배치 삭제 실행
     */
    private int batchDeleteBusinessDays(List<CmptSaleDayPkey> pkeysToDelete) {
        // 배치 크기로 나누어 삭제
        int totalDeleted = 0;
        for (int i = 0; i < pkeysToDelete.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, pkeysToDelete.size());
            List<CmptSaleDayPkey> batch = pkeysToDelete.subList(i, endIndex);
            
            // 존재하는 엔티티만 삭제
            List<CmptSaleDayEntity> entitiesToDelete = cmptSaleDayRepo.findAllById(batch);
            if (!entitiesToDelete.isEmpty()) {
                cmptSaleDayRepo.deleteAll(entitiesToDelete);
                cmptSaleDayRepo.flush();
                totalDeleted += entitiesToDelete.size();
            }
        }
        
        return totalDeleted;
    }
    
    /**
     * 영업일 유효성 검증 (기간별 배치)
     */
    public SYS5030003Dto validateBusinessDaysByPeriod(SYS5030001Dto dto) {
        validateBusinessDayInput(dto);
        
        LocalDate fromDate = LocalDate.parse(dto.getFromSaleDay(), DATE_FORMATTER);
        LocalDate toDate = LocalDate.parse(dto.getToSaleDay(), DATE_FORMATTER);
        
        long totalDays = fromDate.until(toDate).getDays() + 1;
        long existingCount = countExistingBusinessDays(dto);
        long missingCount = totalDays - existingCount;
        
        StringBuilder message = new StringBuilder();
        message.append(String.format("기간: %s ~ %s (%d일)\n", 
            dto.getFromSaleDay(), dto.getToSaleDay(), totalDays));
        message.append(String.format("기존 영업일: %d건\n", existingCount));
        message.append(String.format("누락 영업일: %d건", missingCount));
        
        boolean isValid = missingCount == 0;
        
        return SYS5030003Dto.builder()
            .result(isValid)
            .message(message.toString())
            .build();
                
    }
    
    /**
     * 기존 영업일 개수 조회
     */
    private long countExistingBusinessDays(SYS5030001Dto dto) {
        // 간단한 카운트 쿼리로 성능 최적화
        SYS5030001Vo vo = modelConveter.map(dto, SYS5030001Vo.class);
        return mapper.selectBusinessDay(vo).longValue();
    }
} 