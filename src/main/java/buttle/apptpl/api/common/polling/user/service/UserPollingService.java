package buttle.apptpl.api.common.polling.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.api.common.components.mapper.CommonUserAuthMapper;
import buttle.apptpl.api.common.polling.user.dto.NoticeDto;
import buttle.apptpl.api.common.polling.user.dto.UserPollingDto;
import buttle.apptpl.api.common.polling.user.dto.UserSummaryDto;
import buttle.apptpl.api.common.polling.user.mapper.UserPollingMapper;
import buttle.apptpl.cmm.notification.dto.NotificationsRequestDto;
import buttle.apptpl.cmm.notification.mapper.NotificationMapper;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptUserRepo;
import buttle.apptpl.common.polling.mapper.PollingMapper;
import buttle.apptpl.common.security.bruteforce.LoginAttemptUtils;

/**
 * 관리콘솔 헤더·사이드가 쓰는 폴링.
 *
 * <p>제공하는 것은 둘뿐이다 — <b>배지 건수</b>(`/user-summary`)와 <b>공지 띠 목록</b>(`/notification`).
 *
 * <p>종전에는 `/unified` 가 하나 더 있었다. 같은 요약에 세션 검증·최종접속시각 갱신이 붙은
 * 형태인데 <b>부르는 곳이 없었고</b>, 배지 하나를 위해 60초마다 쓰기와 중복로그인 판정을
 * 돌리는 구조라 되살릴 이유도 없었다. 세션 갱신이 필요하면 `/user-session` 이 그 일만 한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPollingService {

    private final UserPollingMapper userPollingMapper;
    private final PollingMapper pollingMapper;
    private final NotificationMapper notificationMapper;
    private final CommonUserAuthMapper commonUserAuthMapper;
    private final LoginAttemptUtils loginAttemptUtils;
    private final CmptUserRepo cmptUserRepo;

    /** 공지 띠에 한 번에 실어 보낼 최대 건수. 흐르는 문구라 많아야 읽히지 않는다. */
    private static final int NOTICE_MARQUEE_LIMIT = 20;

    /**
     * 현재 사용자 정보 조회
     * @return SecUser 또는 null (anonymous 모드인 경우)
     */
    private String getCurrentUserId() {
        SecUser secUser = SecurityUtils.getCurrentUser();
        // anonymous 모드인 경우 처리하지 않는다.
        if (secUser == null || !secUser.getEnabled()) {
            throw new InvalidationException(473, "사용자 정보가 없습니다.");
        }
        return secUser.getUserId();
    }

    /**
     * 사용자 세션 검증 및 갱신
     * @param ip 접속 IP
     * @param userId 사용자 ID
     * @throws InvalidationException 세션 관련 오류 시
     */
    @Transactional
    private void validateAndUpdateUserSession(String ip, String userId) {
        try {
            // 1. 사용자 정보 테이블에서 사용자 로그인 여부 조회
            CmptUserEntity cmptUserEntity = cmptUserRepo.findById(userId).orElse(null);
            if (cmptUserEntity == null) {
                throw new InvalidationException(473, "사용자 정보가 없습니다.");
            }

            // 중복 로그인 허용 시 체크 안함
            if (!loginAttemptUtils.getAllowDuplicationLogin()) {
                // 사용자 로그인 여부 체크
                if ("N".equals(cmptUserEntity.getLgnYn())) {
                    throw new InvalidationException(473, "세션이 종료되었거나 로그아웃된 사용자입니다.");
                }

                // 접속 IP 비교 (다를 경우 중복로그인 종료 처리)
                if (!ip.equals(cmptUserEntity.getLgnIp())) {
                    throw new InvalidationException(475, "다른 장치에서 로그인이 감지되었습니다.");
                }
            }

            // 사용자 로그인 이력에서 갱신
            commonUserAuthMapper.updateUserLastConnectTime(SecurityUtils.getCurrentUserId());

        } catch (InvalidationException e) {
            // 인증 관련 예외는 그대로 전파
            throw e;
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("사용자 세션 업데이트 중 데이터베이스 오류 발생: userId={}, ip={}, error={}", userId, ip, e.getMessage(), e);
            throw new InvalidationException(500, "데이터베이스 접근 중 오류가 발생했습니다.");
        } catch (org.springframework.transaction.TransactionException e) {
            log.error("사용자 세션 업데이트 중 트랜잭션 오류 발생: userId={}, ip={}, error={}", userId, ip, e.getMessage(), e);
            throw new InvalidationException(500, "트랜잭션 처리 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("사용자 세션 업데이트 중 예상치 못한 오류 발생: userId={}, ip={}, error={}", userId, ip, e.getMessage(), e);
            throw new InvalidationException(500, "세션 업데이트 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자 요약 정보 조회 — 알림 배지의 공급원.
     * @return UserSummaryDto
     */
    @Transactional(readOnly = true)
    public UserSummaryDto getUserSummary() {
        return buildUserSummary(getCurrentUserId());
    }

    /**
     * 사용자 최종 접속 시간 갱신
     * @param ip 접속 IP
     */
    @Transactional
    public void userSession(String ip) {
        validateAndUpdateUserSession(ip, getCurrentUserId());
    }

    /**
     * 공지 띠 목록 + 미확인 건수.
     * @return UserPollingDto
     */
    @Transactional(readOnly = true)
    public UserPollingDto getNotices() {
        String userId;
        try {
            userId = getCurrentUserId();
        } catch (InvalidationException e) {
            log.info("anonymous 모드인 경우 공지 폴링을 처리하지 않는다.");
            return new UserPollingDto("인증되지 않은 사용자입니다.");
        }

        UserSummaryDto userSummaryDto = new UserSummaryDto();
        userSummaryDto.setNoticesCount(pollingMapper.selectPollingNotice(userId));

        return new UserPollingDto(userSummaryDto, getNoticesInternal(userId));
    }

    /**
     * 사용자 요약 정보 구성.
     *
     * <p>종전에는 여섯 개를 채웠다 — 부서/나의/미처리 업무 이관 · 콜백 배정 · 통화 예약 · 예약 메모.
     * 앞의 다섯은 이전 시스템(CNS*)의 상담 기능이고 ChatCall 에 없다. 그 다섯을 띄우던 알림
     * 항목은 §10.106 에서 제거됐고 화면에 소비자가 0 이라 조회도 걷어냈다.
     *
     * @param userId 사용자 ID
     * @return UserSummaryDto
     */
    @Transactional(readOnly = true)
    private UserSummaryDto buildUserSummary(String userId) {

        UserSummaryDto userSummaryDto = new UserSummaryDto();

        // 예약 메모 조회 건수 — 사이드 <마이메모> 배지
        userSummaryDto.setReservationMemosCount(userPollingMapper.selectReservationMemosCount(userId));

        // 미확인 공지 건수 — 알림 배지
        userSummaryDto.setNoticesCount(pollingMapper.selectPollingNotice(userId));

        return userSummaryDto;
    }

    /**
     * 공지 띠에 실을 목록.
     *
     * <p>종전에는 {@code commonKmsAdaptor.getEmergencyNoticeList()} 를 불렀는데 그 구현이
     * <b>빈 배열을 반환하는 스텁</b>이었다(이전 시스템 KMS 연동 자리). 그래서 띠는 언제나
     * 폴백 문구("오늘도 활기찬 하루…")만 돌았다. 지금은 ChatCall 자기 공지를 싣는다.
     *
     * <p>미확인 공지를 앞에 세운다 — 흐르는 문구라 뒤로 갈수록 읽히지 않는다.
     *
     * @param userId 사용자 ID
     * @return 공지 목록
     */
    @Transactional(readOnly = true)
    private List<NoticeDto> getNoticesInternal(String userId) {
        NotificationsRequestDto req = NotificationsRequestDto.builder()
                .mbrId(userId)
                .viewUseYn("Y")
                .build();

        return notificationMapper.selectNotice(req).stream()
                .sorted((a, b) -> {
                    // 미확인(N) 먼저
                    int ra = "N".equals(a.getIsRead()) ? 0 : 1;
                    int rb = "N".equals(b.getIsRead()) ? 0 : 1;
                    return ra - rb;
                })
                .limit(NOTICE_MARQUEE_LIMIT)
                .map(n -> {
                    NoticeDto dto = new NoticeDto();
                    dto.setId(n.getPstId());
                    dto.setNoticeId(n.getPstId());
                    dto.setBoardId(n.getBbsId());
                    dto.setMessage(n.getTtl());
                    // 미확인은 눈에 띄게. 화면(NotificationLayer)이 type 으로 색을 고른다.
                    dto.setType("N".equals(n.getIsRead()) ? "urgent" : "normal");
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
