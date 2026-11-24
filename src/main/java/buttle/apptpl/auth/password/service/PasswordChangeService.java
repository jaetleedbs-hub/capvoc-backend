package buttle.apptpl.auth.password.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.auth.password.dto.COM0020001Dto;
import buttle.apptpl.auth.password.dto.COM0020002Dto;
import buttle.apptpl.auth.password.mapper.PasswordChangeMapper;
import buttle.apptpl.auth.password.vo.COM0020001Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdHstryEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdHstryPkey;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdPlcyEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptUserRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptPswdHstryRepo;
import buttle.apptpl.common.util.ModelConveter;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.auth.password.exception.AccountLockedException;
import buttle.apptpl.api.common.components.service.CommonUserAuthService;
import buttle.apptpl.common.security.crypto.CustomPasswordEncoder;
import buttle.apptpl.common.security.bruteforce.LoginAttemptUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;



/**
 * COM0020 비밀번호변경 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PasswordChangeService {

    private final PasswordChangeMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptUserRepo cmptUserRepo;
    private final CmptPswdHstryRepo cmptPswdHstryRepo;
    private final CustomPasswordEncoder customPasswordEncoder;
    private final LoginAttemptUtils loginAttemptUtils;
    private final CommonUserAuthService commonUserAuthService;

    /** Compact date value converted to midnight in the {@code usr.pswd_chg_dttm} timestamp column. */
    private static final DateTimeFormatter PSWD_CHG_DT_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 비밀번호 재사용 검사에서 볼 이력의 상한.
     *
     * <p>BCrypt 비교는 <b>행마다 한 번씩</b> 돌아야 하고(솔트가 달라 일괄 비교가 불가능하다)
     * 의도적으로 느린 해시다. 주기 안의 이력이 많은 계정에서 변경 요청이 눈에 띄게 느려지지 않도록
     * 최근 것부터 이 개수까지만 본다.
     *
     * <p>«몇 세대까지 막을 것인가»는 정책 수치라 사람이 정할 몫이다
     * ({@code docs/quality/조사-계정정책-임직원데이터-20260905.md} §5-2 #4).
     * 이 값은 그 결정을 대신하는 것이 아니라, 결정 전까지의 <b>작업량 상한</b>이다.
     */
    private static final int REUSE_CHECK_HISTORY_LIMIT = 20;

    /** 정책에 주기가 비어 있거나 0 이하일 때 쓸 기본 일수 — 종전 SQL 의 {@code coalesce(..., 180)} 과 같다. */
    private static final int DEFAULT_PSWD_CHG_CYC_DAYS = 180;

    /**
     * 비밀번호 변경
     * @param dto 비밀번호변경 요청
     * @return 비밀번호변경 응답
     */
    public COM0020002Dto updatePassword(COM0020001Dto dto) {
        // 필수 필드 검증
        if(dto.getNewPassword().equals(dto.getCurrentPassword())) {
            dto.setMessage("현재 비밀번호와 바꿀 비밀번호가 같습니다.");
            return COM0020002Dto.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .resultCode("SAME_AS_CURRENT")
                .build();
        }

        if (ObjectUtils.isEmpty(dto.getUserId())) {
            dto.setMessage("사용자 ID를 입력하세요.");
            return COM0020002Dto.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .resultCode("MISSING_USER_ID")
                .build();
        }
        if (ObjectUtils.isEmpty(dto.getCurrentPassword())) {
            dto.setMessage("현재 비밀번호를 입력하세요.");
            return COM0020002Dto.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .resultCode("MISSING_CURRENT")
                .build();
        }
        if (ObjectUtils.isEmpty(dto.getNewPassword())) {
            dto.setMessage("새 비밀번호를 입력하세요.");
            return COM0020002Dto.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .resultCode("MISSING_NEW")
                .build();
        }
        if (ObjectUtils.isEmpty(dto.getConfirmPassword())) {
            dto.setMessage("새 비밀번호 확인을 입력하세요.");
            return COM0020002Dto.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .resultCode("MISSING_CONFIRM")
                .build();
        }

        // 새 비밀번호와 확인 비밀번호 일치 검증
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            dto.setMessage("새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
            return COM0020002Dto.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .resultCode("CONFIRM_MISMATCH")
                .build();
        }

        // 1. 사용자 정보 조회 및 기존 비밀번호 검증
        Optional<CmptUserEntity> userEntityOpt = cmptUserRepo.findById(dto.getUserId());
        if (!userEntityOpt.isPresent()) {
            dto.setMessage("사용자 정보를 찾을 수 없습니다.");
            return COM0020002Dto.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .resultCode("USER_NOT_FOUND")
                .build();
        }

        CmptUserEntity userEntity = userEntityOpt.get();

        // 기존 비밀번호 검증
        boolean isCurrentPasswordValid = customPasswordEncoder.matches(dto.getCurrentPassword(), userEntity.getPswd());
        if (!isCurrentPasswordValid) {
            // 로그인 실패 처리 (실패 횟수 증가) 및 메시지 받기
            String failureMessage = loginAttemptUtils.loginFailed(userEntity.getUserId(), userEntity.getLgnIp());
            
            // 실패 처리 후 최신 사용자 정보 조회
            CmptUserEntity updatedUser = cmptUserRepo.findById(dto.getUserId()).get();
            
            // 계정이 잠긴 경우 계정 중지 후 잠금 전용 예외 발생
            // (컨트롤러가 메시지 텍스트가 아닌 예외 타입으로 계정잠금을 식별한다)
            if ("N".equals(updatedUser.getLgnYn())) {
                commonUserAuthService.userLogout(dto.getUserId());
                throw new AccountLockedException(failureMessage);
            }

            throw new InvalidationException(failureMessage);
        }

        // 2. 비밀번호 정책 검증
        validatePasswordPolicy(dto.getNewPassword(), dto.getUserId());

        // 3. 비밀번호 변경 처리
        // 비밀번호 암호화
        String encodedNewPassword = customPasswordEncoder.encode(dto.getNewPassword());
        String currentDate = LocalDateTime.now().format(PSWD_CHG_DT_FORMAT);

        CmptPswdHstryPkey pswdHstryPkey = CmptPswdHstryPkey.builder()
                .userId(dto.getUserId())
                .chgDt(LocalDateTime.now())
                .build();
        
        CmptPswdHstryEntity pswdHstryEntity = CmptPswdHstryEntity.builder()
                .id(pswdHstryPkey)
                .pswd(encodedNewPassword)
                .build();
        
        cmptPswdHstryRepo.save(pswdHstryEntity);

        // 사용자 정보 업데이트
        userEntity.setPswd(encodedNewPassword);
        userEntity.setPswdChgDt(currentDate);
        userEntity.setLgnTryCnt(0);
        cmptUserRepo.save(userEntity);

        // 응답 DTO 생성
        COM0020002Dto result = modelConveter.map(userEntity, COM0020002Dto.class);
        result.setMessage("비밀번호가 성공적으로 변경되었습니다.\n보안을 위해 다시 로그인해 주십시오.");
        
        return COM0020002Dto.builder()
            .userId(result.getUserId())
            .lgnTryCnt(result.getLgnTryCnt())
            .message(result.getMessage())
            .resultCode("SUCCESS")
            .build();
    }

    /**
     * 비밀번호 정책 검증
     *
     * <p>⛔ {@code newPassword} 는 <b>원문</b>이다. 로그·예외 메시지·응답 어디에도 싣지 않는다 —
     * 이 메서드가 밖으로 내보내는 것은 «위반했다/아니다» 와 어느 규칙을 어겼는지뿐이다.
     *
     * @param newPassword 새 비밀번호 (원문)
     * @param userId 사용자 ID
     */
    private void validatePasswordPolicy(String newPassword, String userId) {
        // 비밀번호 정책 조회
        CmptPswdPlcyEntity pswdPlcy = commonUserAuthService.getPswdPlcy();

        // 최소 길이 검증
        if (pswdPlcy.getPswdMinLen().intValue() > newPassword.length()) {
            throw new InvalidationException("비밀번호는 최소 " + pswdPlcy.getPswdMinLen() + "자 이상이어야 합니다.");
        }

        // 대문자 검증
        if ("Y".equals(pswdPlcy.getPswdUcReqYn())) {
            if (!newPassword.matches(".*[A-Z].*")) {
                throw new InvalidationException("비밀번호에 대문자를 포함해야 합니다.");
            }
        }
        // 소문자 검증
        if ("Y".equals(pswdPlcy.getPswdLcReqYn())) {
            if (!newPassword.matches(".*[a-z].*")) {
                throw new InvalidationException("비밀번호에 소문자를 포함해야 합니다.");
            }
        }
        
        // 숫자 검증
        if ("Y".equals(pswdPlcy.getPswdNumReqYn())) {
            if (!newPassword.matches(".*[0-9].*")) {
                throw new InvalidationException("비밀번호에 숫자를 포함해야 합니다.");
            }
        }
        
        // 특수문자 검증
        if ("Y".equals(pswdPlcy.getPswdSpcCharReqYn())) {
            if (!newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~].*")) {
                throw new InvalidationException("비밀번호에 특수문자를 포함해야 합니다.");
            }
        }

        // 비밀번호 재사용 검증 (재사용 여부가 N인 경우)
        if("N".equals(pswdPlcy.getPswdReuseYn())) {
            if (isPasswordReused(userId, newPassword, pswdPlcy)) {
                throw new InvalidationException("최근 기간 내에 사용한 비밀번호입니다.");
            }
        }

        // 금지 문자 검증
        String denyChars = pswdPlcy.getPswdDenyChar();
        if(denyChars != null && !denyChars.trim().isEmpty()) {
            // ','로 구분된 금지 문자들을 배열로 분리
            String[] denyCharArray = denyChars.split(",");
            
            for(String denyChar : denyCharArray) {
                String trimmedDenyChar = denyChar.trim();
                if(!trimmedDenyChar.isEmpty() && newPassword.contains(trimmedDenyChar)) {
                    throw new InvalidationException("비밀번호에 금지 문자 '" + trimmedDenyChar + "'가 포함되어 있습니다.");
                }
            }   
        }

        
    }

    /**
     * 비밀번호 검증
     * @param dto 비밀번호 검증 요청
     * @return 비밀번호 검증 결과
     */
    @Transactional(readOnly = true)
    public COM0020001Dto validatePassword(COM0020001Dto dto) {
        COM0020001Vo vo = modelConveter.map(dto, COM0020001Vo.class);
        COM0020001Vo result = mapper.validatePassword(vo);
        return modelConveter.map(result, COM0020001Dto.class);
    }

    /**
     * 정책 주기 안의 이력에 <b>같은 비밀번호</b>가 있는지 확인한다.
     *
     * <p><b>왜 SQL 이 아니라 Java 인가.</b> 종전 구현은 매퍼가
     * {@code WHERE user_id = #{userId} AND pswd = #{pswd}} 로 <b>해시 문자열을 동등비교</b>했다.
     * 저장 형식은 BCrypt 이고({@link CustomPasswordEncoder} extends {@code BCryptPasswordEncoder})
     * BCrypt 는 호출마다 무작위 솔트를 쓰므로, 같은 비밀번호를 다시 넣어도 해시가 매번 다르다.
     * 즉 그 조건은 <b>언제나 거짓</b>이었고 {@code count} 는 <b>항상 0</b> 이었다 —
     * 정책({@code pswd_reuse_yn='N'})이 켜져 있고 코드가 그 분기를 타는데도 아무것도 막지 못했다.
     * 「검사가 없다」보다 나쁘다. 있는 것처럼 보이기 때문이다.
     *
     * <p>BCrypt 는 <b>원문 없이 비교할 수 없다.</b> 그래서 이 판정은 SQL 로 옮길 수 없고,
     * 원문을 손에 쥔 이 자리에서 이력 해시마다 {@code matches()} 를 돌려야만 성립한다.
     *
     * <p>⛔ {@code rawPassword} 를 로그·예외 메시지·응답에 싣지 않는다. 밖으로 나가는 것은
     * «맞았다/아니다» 한 비트뿐이다.
     *
     * @param userId      사용자 ID
     * @param rawPassword <b>원문</b> 새 비밀번호
     * @param pswdPlcy    비밀번호 정책 — 주기(일)를 여기서 읽는다
     * @return 주기 안의 이력에 같은 비밀번호가 있으면 true
     */
    private boolean isPasswordReused(String userId, String rawPassword, CmptPswdPlcyEntity pswdPlcy) {
        int days = DEFAULT_PSWD_CHG_CYC_DAYS;
        if (pswdPlcy.getPswdChgCycCnt() != null && pswdPlcy.getPswdChgCycCnt().intValue() > 0) {
            days = pswdPlcy.getPswdChgCycCnt().intValue();
        }

        List<String> recentHashes =
                mapper.selectRecentPasswordHashes(userId, days, REUSE_CHECK_HISTORY_LIMIT);
        if (recentHashes == null || recentHashes.isEmpty()) {
            return false;
        }

        for (String storedHash : recentHashes) {
            if (customPasswordEncoder.matches(rawPassword, storedHash)) {
                return true;
            }
        }
        return false;
    }
} 