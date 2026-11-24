package buttle.apptpl.log.userchange.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.log.userchange.dto.SYS6030001Dto;
import buttle.apptpl.log.userchange.dto.SYS6030002Dto;
import buttle.apptpl.log.userchange.mapper.UserChangeHistoryMapper;
import buttle.apptpl.log.userchange.vo.SYS6030001Vo;
import buttle.apptpl.log.userchange.vo.SYS6030002Vo;
import buttle.apptpl.common.util.AuthOgnzUserUtils;
import buttle.apptpl.common.util.ModelConveter;

/**
 * SYS6030 사용자변경이력 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserChangeHistoryService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final UserChangeHistoryMapper mapper;
    private final ModelConveter modelConveter;

    /**
     * 사용자 변경 이력 조회
     * @param dto 사용자변경이력 조회 요청
     * @return 사용자변경이력 조회 응답
     */
    public Page<SYS6030002Dto> selectUserChgHistList(SYS6030001Dto dto, Pageable pageable) {
        if(dto.getStartDate() == null || dto.getEndDate() == null) {
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

        SYS6030001Vo vo = modelConveter.map(dto, SYS6030001Vo.class);

        vo.setOgnzCd(AuthOgnzUserUtils.getAuthOgnzCd(vo.getOgnzCd()));
        vo.setUserId(AuthOgnzUserUtils.getAuthUserId(vo.getUserId()));

        Page<SYS6030002Vo> resultPage = mapper.selectUserChgHistList(vo, pageable);
        return resultPage.map(voItem -> modelConveter.map(voItem, SYS6030002Dto.class));
    }

    /**
     * 사용자 변경 이력 엑셀 출력
     * @param dto 사용자변경이력 조회 요청
     * @return 사용자변경이력 조회 응답
     */
    public Page<SYS6030002Dto> exportExcelUserChgHistList(SYS6030001Dto dto, Pageable pageable) {
        SYS6030001Vo vo = modelConveter.map(dto, SYS6030001Vo.class);
        if( vo == null){
            throw new InvalidationException("전달된 값이 올바르지 않습니다.");
        }
        
        vo.setOgnzCd(AuthOgnzUserUtils.getAuthOgnzCd(vo.getOgnzCd()));
        vo.setUserId(AuthOgnzUserUtils.getAuthUserId(vo.getUserId()));

        Page<SYS6030002Vo> resultPage = mapper.selectUserChgHistList(vo, pageable);
        return resultPage.map(voItem -> modelConveter.map(voItem, SYS6030002Dto.class));
    }
} 