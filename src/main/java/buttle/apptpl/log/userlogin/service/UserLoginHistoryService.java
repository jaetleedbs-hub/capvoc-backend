package buttle.apptpl.log.userlogin.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.log.userlogin.dto.SYS6010001Dto;
import buttle.apptpl.log.userlogin.dto.SYS6010002Dto;
import buttle.apptpl.log.userlogin.mapper.UserLoginHistoryMapper;
import buttle.apptpl.log.userlogin.vo.SYS6010001Vo;
import buttle.apptpl.log.userlogin.vo.SYS6010002Vo;
import buttle.apptpl.common.util.AuthOgnzUserUtils;
import buttle.apptpl.common.util.ModelConveter;

/**
 * SYS6010 사용자로그인이력 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLoginHistoryService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final UserLoginHistoryMapper mapper;
    private final ModelConveter modelConveter;

    /**
     * 일별 사용자 로그인 이력 조회
     * @param dto 사용자로그인이력 조회 요청
     * @return 사용자로그인이력 조회 결과 목록
     */
    public Page<SYS6010002Dto> selectDailyUserLoginHistList(SYS6010001Dto dto, Pageable pageable) {
        // 필수 조건 검증
        if (ObjectUtils.isEmpty(dto.getStartDate()) || ObjectUtils.isEmpty(dto.getEndDate())) {
            throw new InvalidationException("조회 기간을 입력하세요.");
        }

        try {
            LocalDate fromDate = LocalDate.parse(dto.getStartDate(), DATE_FORMATTER);
            LocalDate toDate = LocalDate.parse(dto.getEndDate(), DATE_FORMATTER);

            if(fromDate.isAfter(toDate)) {
                throw new InvalidationException("시작일이 종료일보다 늦을 수 없습니다.");
            }

            if(fromDate.isAfter(LocalDate.now())) {
                throw new InvalidationException("종료일이 현재보다 빠를 수 없습니다.");
            }

            if(fromDate.plusMonths(1).isBefore(toDate)) {
                throw new InvalidationException("조회 기간은 최대 1개월까지 가능합니다.");
            }
        } catch (DateTimeParseException e) {
            throw new InvalidationException("날짜 형식이 올바르지 않습니다. (YYYYMMDD 형식으로 입력하세요)");
        }

        SYS6010001Vo vo = modelConveter.map(dto, SYS6010001Vo.class);
        vo.setOgnzCd(AuthOgnzUserUtils.getAuthOgnzCd(vo.getOgnzCd()));
        vo.setUserId(AuthOgnzUserUtils.getAuthUserId(vo.getUserId()));

        Page<SYS6010002Vo> resultPage = mapper.selectDailyUserLoginHistList(vo, pageable);
        return resultPage.map(voItem -> modelConveter.map(voItem, SYS6010002Dto.class));
    }

    /**
     * 일별 사용자 로그인 이력 엑셀 출력
     * @param dto 사용자로그인이력 조회 요청
     * @return 처리 건수
     */
    // @Transactional(readOnly = true)
    // public Integer exportExcelDailyUserLoginHistList(SYS6010001Dto dto, Pageable pageable) {
    //     // 조회 조건 검증
    //     if (ObjectUtils.isEmpty(dto.getStartDate()) || ObjectUtils.isEmpty(dto.getEndDate())) {
    //         throw new InvalidationException("조회 기간을 입력하세요.");
    //     }

    //     // 데이터 조회
    //     Page<SYS6010002Dto> dataList = selectDailyUserLoginHistList(dto, pageable);
        
    //     if (dataList.isEmpty()) {
    //         throw new InvalidationException("출력할 데이터가 없습니다.");
    //     }

    //     return dataList.getTotalElements().intValue();
    // }
} 