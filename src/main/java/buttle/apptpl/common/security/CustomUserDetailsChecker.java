package buttle.apptpl.common.security;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.vo.CustomUserDetails;
import micoai.corebase.util.CoreUtils;
import buttle.apptpl.common.exception.CustomAuthenticationException;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.security.bruteforce.LoginAttemptUtils;
import buttle.apptpl.common.security.util.SecurityMaskingUtil;

@Slf4j
@Component

public class CustomUserDetailsChecker implements UserDetailsChecker {

	@Autowired
	private LoginAttemptUtils loginAttemptUtils;

	@Override
	public void check(UserDetails userDetails) {
		
		CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
		CmptUserEntity userEntity = customUserDetails.getUserEntity();
		if (loginAttemptUtils.isAccountExpired(userEntity)) {
			log.warn("사용할 수 없는 사용자 계정입니다: {}", userEntity.getUserId());
			// 계정만료 설정
			throw new CustomAuthenticationException("시스템을 사용할 수 없는 사용자입니다.");
		}

		if (loginAttemptUtils.isUserHasLapsedLastLogin(userEntity.getUserId())) {
			log.warn("사용자 최종 로그인 기준 경과여부 확인: {}", userEntity.getUserId());
			throw new CustomAuthenticationException("최종 사용일이 3개월이상 경과한 사용자는 시스템을 사용할 수 없습니다.");
		}

		if (loginAttemptUtils.isAccountLocked(userEntity)) {
			log.info("계정 잠금 사용자입니다: {}", userEntity.getUserId());
			throw new CustomAuthenticationException("비밀번호 오류로 사용자 계정 사용이 중지되었습니다.\r\n관리자에게 잠금해제 요청하세요");
		}

		// 초기화된 비밀번호 시
		if( loginAttemptUtils.isInitialPassword(userEntity)) {
			log.warn("초기 비밀번호입니다: {}", userEntity.getUserId());
			throw new CustomAuthenticationException("초기 비밀번호입니다. 비밀번호를 변경하세요.", "INITIAL_PSWD");
		}

		// 비밀번호 변경일자 도래
		if( loginAttemptUtils.isCredentialsExpired(userEntity)) {
			log.warn("비밀번호 변경일자가 도래했습니다: {}", userEntity.getUserId());
			throw new CustomAuthenticationException("비밀번호 변경일자가 도래했습니다. 비밀번호를 변경하세요.", "CHANGE_PSWD");
		}

		// 중복 로그인 체크
		if( loginAttemptUtils.isDuplicateLogin(userEntity)) {
			log.warn("중복 로그인 사용자입니다: {}", userEntity.getUserId());
			Map<String, Object> source = new HashMap<>();
			source.put("userId", userEntity.getUserId());
			source.put("userIp", CoreUtils.webutils.getRemoteIp());
			source.put("ip", userEntity.getLgnIp());
			source.put("timestamp", System.currentTimeMillis());

			String encryptedJson = "";
			try{
				encryptedJson = SecurityMaskingUtil.encryptSimple(CoreUtils.toJsonFromMap(source));
			} catch( Exception e) {
				throw new CustomAuthenticationException("", "DUPLICATE_LOGIN");
			}
			throw new CustomAuthenticationException(encryptedJson, "DUPLICATE_LOGIN");
		}

	}

}
