package buttle.apptpl.common.security.bruteforce;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.util.CoreUtils;
import buttle.apptpl.api.common.components.dto.CurrentDatetimeDto;
import buttle.apptpl.api.common.components.service.CommonComponentService;
import buttle.apptpl.api.common.components.service.CommonUserAuthService;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdPlcyEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptSysCrtrEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserLgnHstryEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserLgnHstryPkey;
import buttle.apptpl.common.model.cmpt.repository.CmptUserLgnHstryRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptUserRepo;
import buttle.apptpl.common.security.SvcEnvConfig;
import buttle.apptpl.common.security.crypto.CustomPasswordEncoder;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginAttemptUtils {

	// 사용자 정보 repository
	private final CmptUserRepo cmptUserRepo;

	private final CommonComponentService commonComponentService;

	private final CommonUserAuthService commonUserAuthService;

	private final static String DUPLICATE_LOGIN_TIME = "DUPLICATE_LOGIN_TIME";
	private final static String DUPLICATE_LOGIN_ALLOW = "DUPLICATE_LOGIN_ALLOW";

	private final SvcEnvConfig svcEnvConfig;

	private final CustomPasswordEncoder customPasswordEncoder;

	/**
	 * 비밀번호 변경 기준일 조회
	 * @return
	 */
	private int getPswdChgCycCnt() {
		CmptPswdPlcyEntity cmptPswdPlcyEntity = commonUserAuthService.getPswdPlcy();
		if( cmptPswdPlcyEntity != null){
			return cmptPswdPlcyEntity.getPswdChgCycCnt().intValue();
		}
		return svcEnvConfig.getLoginAttempt().getInitialPswdDays();
	}

	/**
	 * 로그인 실패 횟수 조회
	 * @return
	 */
	public int getLgnTryPsbltyCnt() {
		CmptPswdPlcyEntity cmptPswdPlcyEntity = commonUserAuthService.getPswdPlcy();
		if( cmptPswdPlcyEntity != null){
			return cmptPswdPlcyEntity.getLgnTryPsbltyCnt().intValue();
		}
		return svcEnvConfig.getLoginAttempt().getLoginFailCount();
	}


	/**
	 * 중복로그인 체크 시간 조회
	 * @return
	 */
	private int getDuplicateLoginTime() {
		return svcEnvConfig.getLoginAttempt().getDuplicateLoginTime();
	}

	/**
	 * 중복로그인 허용 여부 조회
	 * @return
	 */
	public boolean getAllowDuplicationLogin() {
		return svcEnvConfig.getLoginAttempt().getAllowDuplicationLogin();
	}



	/**
	 * 초기화된 비밀번호 여부 확인 (사용자의 사번과 동일한가?)
	 * @param userId
	 * @return
	 */
	public boolean isInitialPassword(CmptUserEntity userEntity) {
		return customPasswordEncoder.matches(userEntity.getUserId(), userEntity.getPswd());
	}

	/** 날짜 문자열의 표준 형식. 쓰는 쪽·읽는 쪽이 공유한다. */
	private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

	/** {@code yyyyMMdd} 8자리 + (있어도 되는) 뒤따르는 시각 자리. */
	private static final Pattern YYYYMMDD_PREFIX = Pattern.compile("^(\\d{8})\\d*$");

	/**
	 * 날짜 문자열에서 {@code yyyyMMdd} 8자리를 뽑아낸다 — <b>읽는 쪽을 관대하게</b>.
	 *
	 * <p>{@code cdm_own.usr.pswd_chg_dttm} 은 timestamp이고 JPA 변환기는 이를
	 * {@code yyyyMMddHHmmss} 문자열로 돌려준다. 비밀번호 변경 경로는 날짜만 전달할 수도
	 * 있으므로 8자리와 시각이 붙은 형식을 모두 읽는다.
	 *
	 * <p>⚠ <b>«못 읽으면 만료»(fail-closed)로 뒤집지 말 것.</b> 형식이 하나라도 어긋나는 순간
	 * 전 계정이 비밀번호 변경 화면으로 몰려 콘솔이 막힌 것처럼 보인다. fail-open 을 없애는 길은
	 * 판정을 뒤집는 것이 아니라 <b>읽을 수 있는 형식의 폭을 넓히는 것</b>이다.
	 *
	 * @param dateString 날짜 문자열 ({@code yyyyMMdd}, 또는 그 뒤에 시각이 붙은 형태)
	 * @return {@code yyyyMMdd} 8자리. 뽑아낼 수 없으면 {@code null}
	 */
	static String extractYyyyMMdd(String dateString) {
		if (StringUtils.isBlank(dateString)) {
			return null;
		}
		Matcher matcher = YYYYMMDD_PREFIX.matcher(dateString.trim());
		return matcher.matches() ? matcher.group(1) : null;
	}

    /**
     * 주어진 날짜 문자열로부터 오늘까지 며칠이 경과했는지 계산합니다.
     * 날짜 형식은 'yyyyMMdd' 이며, 뒤에 시각이 붙어 있어도({@code yyyyMMddHHmmss}) 앞 8자로 읽습니다.
     *
     * @param dateString 날짜 문자열 (예: "20240101", "20240101093000")
     * @return 오늘까지 경과한 일수. 입력 날짜가 오늘보다 미래라면 음수 값 반환.
     *         읽어 낼 수 없으면 0 — <b>의도적인 fail-open</b> ({@link #extractYyyyMMdd} 주석 참조)
     */
    private long getDaysSinceGivenDate(String dateString) {

		if( StringUtils.isEmpty(dateString)) {
			return 0;
		}

        // 현재 날짜 시간 조회
        // CurrentDatetimeDto currentDatetimeDto = commonComponentService.getCurrentDatetime();

        // 1. 오늘 날짜를 가져옵니다.
        //LocalDate today = LocalDate.parse(currentDatetimeDto.getCurrentDate(), DateTimeFormatter.ofPattern("yyyyMMdd")); // 현재 날짜 (예: 2025-07-17)

        try {
            // 2. 입력된 날짜 문자열에서 yyyyMMdd 8자리를 뽑습니다.
            String givenYmd = extractYyyyMMdd(dateString);
            if (givenYmd == null) {
                log.warn("잘못된 날짜 형식입니다. 예상: yyyyMMdd(뒤에 시각이 붙어도 됨), 실제: '{}' (길이: {})",
                        dateString, dateString.length());
                return 0; // 읽어 낼 수 없으면 0일 반환 (만료되지 않은 것으로 처리 — fail-open)
            }

            LocalDate givenDate = LocalDate.parse(givenYmd, YYYYMMDD);

            // 3. 오늘 날짜를 가져옵니다. (was의 시간을 사용합니다.)
            LocalDate today = LocalDate.now();

            // 4. 두 날짜 간의 일수 차이를 계산합니다.
            // ChronoUnit.DAYS.between(날짜1, 날짜2)는 날짜1부터 날짜2까지의 일수를 반환합니다.
            // 따라서 'givenDate'부터 'today'까지의 일수를 계산하면 경과 일수가 됩니다.
            return ChronoUnit.DAYS.between(givenDate, today);

        } catch (DateTimeParseException e) {
            log.error("날짜 파싱 오류 - 입력값: '{}', 오류: {}", dateString, e.getMessage());
            return 0; // 파싱 오류 시 0일 반환 (만료되지 않은 것으로 처리 — fail-open)
        }
    }

	/**
	 * 비밀번호 변경일자가 도래했는지 확인
	 * @param userEntity
	 * @return
	 */
	public boolean isCredentialsExpired(CmptUserEntity userEntity) {

		int initialPswdDays = getPswdChgCycCnt();

		String pswdChgDt = userEntity.getPswdChgDt();
		// 비밀번호 변경일시가 없으면 초기화된 비밀번호 시
		if( pswdChgDt == null || StringUtils.isEmpty(pswdChgDt)) {
			return true;
		}

		return getDaysSinceGivenDate(pswdChgDt) >= initialPswdDays;
	}


	/**
	 * 중복 로그인 체크
	 * 로그인 상태가 'Y'이고 로그인 IP가 서로 다르고 
	 * 최근 로그인 체크 일시와 현재 로그인 일시가 1분 이내라면 중복 로그인으로 간주
	 * @param userId
	 * @return
	 */
	public boolean isDuplicateLogin(CmptUserEntity userEntity) {

		// 로그인 상태가 'N'이면 중복로그인 체크 안함
		if( !userEntity.getLgnYn().equals("Y")) {
			return false;
		}

		// 중복로그인 허용 여부 체크
		if( getAllowDuplicationLogin()) {
			return false;
		}

		// 이전에 로그인한 사용자 ip가 현재 접속 ip와 동일하면 중복 로그인 아님
		String remoteIp = CoreUtils.webutils.getRemoteIp();
		if( remoteIp != null && Objects.equals(remoteIp, userEntity.getLgnIp())) {
			return false;
		}

		// 최그 로그인 체크 일시와 현재 로그인 일시가 지정된 시간 이내라면 중복 로그인으로 간주
		return commonUserAuthService.selectLastUserConnectTime(userEntity.getUserId()) < getDuplicateLoginTime();
	}


   /**
	 * 로그인 시도실패 시 처리
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public String loginFailed(String userId, String ip) {
		
		// userId null 체크
		if (userId == null || userId.trim().isEmpty()) {
			return "사용자 ID가 입력되지 않았습니다.";
		}
		
		// 사용자 로그인 정보 확인
		CmptUserEntity userEntity = cmptUserRepo.findById(userId).orElse(null);
		if (userEntity == null) {
			return "사용자 정보를 찾을 수 없습니다.";
		}
		
		if( isAccountLocked(userEntity)) {
			return "비밀번호 오류로 사용자 계정 사용이 중지되었습니다.\r\n관리자에게 잠금해제 요청하세요";
		}

		// 로그인 시도횟수 증가
		int userTryCnt = 0;
		if( userEntity.getLgnTryCnt() != null) {
			userTryCnt = userEntity.getLgnTryCnt().intValue();
		}

		userEntity.setLgnTryCnt(userTryCnt + 1);
		cmptUserRepo.save(userEntity);

		int maxAttemptCnt = getLgnTryPsbltyCnt();
		return String.format("아이디 또는 비밀번호가 틀렸습니다. %d회 남았습니다.", maxAttemptCnt - userTryCnt);
    }

	/**
	 * 계정 만료 여부 조회
	 * @param userEntity
	 * @return
	 */
	public boolean isAccountExpired(CmptUserEntity userEntity) {
		if(!"Y".equals(userEntity.getUseYn())){
			return true;
		}

		// 입사일자가 없거나 오늘날짜 이후라면
		if(StringUtils.isEmpty(userEntity.getJncmpYmd()) || getDaysSinceGivenDate(userEntity.getJncmpYmd()) < 0) {
			return true;
		}

		// 퇴사일자가 있고 오늘날짜 이전이라면 
		if( StringUtils.isNotEmpty(userEntity.getRsgntnYmd()) && getDaysSinceGivenDate(userEntity.getRsgntnYmd()) > 0) {
			return true;
		}

		return false;
	}

	/**
	 * 계정이 <b>잠겼는가</b> — 실패 횟수가 정책 임계에 도달했으면 {@code true}.
	 *
	 * <p><b>이름을 바꿨다(2026-09-05).</b> 종전 이름은 {@code isAccountNonLocked} 였는데
	 * 돌려주는 값은 {@code userTryCnt >= maxAttemptCnt} — 즉 <b>잠겼을 때 {@code true}</b> 로
	 * 이름과 정반대였다. 두 호출부는 {@code if (…) throw} 형태라 <b>우연히 맞게</b> 쓰고 있었다.
	 *
	 * <p>⚠ 이 이름이 위험했던 이유는 버그가 아니라 <b>다음 사람</b>이다.
	 * Spring 의 {@code UserDetails.isAccountNonLocked()} 는 <b>잠기지 않았을 때 true</b> 라는
	 * 정반대 계약이다. 이름이 같으니 그대로 잇고 싶어지는데, 그러면 <b>잠금이 통째로 뒤집혀
	 * 잠긴 계정만 통과</b>한다. 실제로 이 세션에서 두 번 그렇게 읽혔다.
	 *
	 * @return 잠김이면 {@code true}
	 */
    public boolean isAccountLocked(CmptUserEntity userEntity) {
		
		int maxAttemptCnt = getLgnTryPsbltyCnt();

		int userTryCnt = 0;
		if( userEntity.getLgnTryCnt() != null) {
			userTryCnt = userEntity.getLgnTryCnt().intValue();
		}

		// 로그인 시도횟수 확인
		return userTryCnt >= maxAttemptCnt;
    }


    @Autowired
    private CmptUserRepo userRepo;


    @Autowired
    private CmptUserLgnHstryRepo userLgnHstryRepo;

    /**
     * 로그인 성공 처리 (사용자테이블)
     * @param request
     * @param member
     */
    public void loginSucceeded(CmptUserEntity userEntity) {
    	
    	// 로그인 성공 처리 (사용자테이블)
		userEntity.setLgnYn("Y");
        userEntity.setLgnTryCnt(0);
    	userRepo.save(userEntity);

        // 현재 날짜 시간 조회
        CurrentDatetimeDto currentDatetimeDto = commonComponentService.getCurrentDatetime();




        // 로그인 이력 갱신
        CmptUserLgnHstryPkey key = CmptUserLgnHstryPkey.builder().lgnYmd(currentDatetimeDto.getCurrentDate()).userId(userEntity.getUserId()).build();
        CmptUserLgnHstryEntity userLgnHstryEntity = userLgnHstryRepo.findById(key).orElse(null);
        if (userLgnHstryEntity == null) {
            userLgnHstryEntity = new CmptUserLgnHstryEntity();
            userLgnHstryEntity.setId(key);
            userLgnHstryEntity.setLgnIp(userEntity.getLgnIp());
			userLgnHstryEntity.setLastCntnIdntyDt(currentDatetimeDto.getCurrentTimestamp());
            userLgnHstryEntity.setLgnDt(currentDatetimeDto.getCurrentTimestamp());
            userLgnHstryEntity.setLgnCnt(BigDecimal.ONE);
        } else {
            userLgnHstryEntity.setLgnCnt(userLgnHstryEntity.getLgnCnt().add(BigDecimal.ONE));
			userLgnHstryEntity.setLastCntnIdntyDt(currentDatetimeDto.getCurrentTimestamp());
        }
        userLgnHstryRepo.save(userLgnHstryEntity);


		// 근태현황 등록
		commonUserAuthService.updateWorkState(userEntity.getUserId());
    }

	
	/**
	 * 사용자 최종 로그인 기준 경과여부 확인
	 * @param userId
	 * @return
	 */
	public boolean isUserHasLapsedLastLogin(String userId) {
		return !"N".equals(commonUserAuthService.selectUserHasLapsedLastLogin(userId));
	}
	
}