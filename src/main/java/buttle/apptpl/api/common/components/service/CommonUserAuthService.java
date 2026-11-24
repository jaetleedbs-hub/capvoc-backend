package buttle.apptpl.api.common.components.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.exception.InvalidationException;
import micoai.corebase.util.CoreUtils;
import buttle.apptpl.api.common.components.dto.LoginUserDto;
import buttle.apptpl.api.common.components.mapper.CommonUserAuthMapper;
import buttle.apptpl.common.config.caffeine.CacheType;
import buttle.apptpl.common.model.cmpt.entity.CmptAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptOgnzEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdPlcyEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptSysCrtrEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptAuthrtRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptOgnzRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptPswdPlcyRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptSysCrtrRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptUserLgnHstryRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptUserRepo;
import buttle.apptpl.common.security.SvcEnvConfig;
import buttle.apptpl.common.security.util.SecurityMaskingUtil;

@Slf4j
@Service
@Component
@Getter
public class CommonUserAuthService {
    
    
    @Autowired
    private SvcEnvConfig svcEnvConfig;
    
    private final CacheManager cacheManager;
    private final CommonUserAuthMapper commonUserAuthMapper;
    private final CmptUserRepo cmptUserRepo;
    private final CmptAuthrtRepo cmptAuthrtRepo;
    private final CmptOgnzRepo cmptOgnzRepo;
    private final CmptPswdPlcyRepo cmptPswdPlcyRepo;
    private final CmptUserLgnHstryRepo cmptUserLgnHstryRepo;
    private final Cache envCache;
    private final Cache sessionUserCache;

    public CommonUserAuthService(
            CacheManager cacheManager,
            CommonUserAuthMapper commonUserAuthMapper,
            CmptUserRepo cmptUserRepo,
            CmptAuthrtRepo cmptAuthrtRepo,
            CmptOgnzRepo cmptOgnzRepo,
            CmptPswdPlcyRepo cmptPswdPlcyRepo,
            CmptUserLgnHstryRepo cmptUserLgnHstryRepo) {
        this.cacheManager = cacheManager;
        this.commonUserAuthMapper = commonUserAuthMapper;
        this.cmptUserRepo = cmptUserRepo;
        this.cmptAuthrtRepo = cmptAuthrtRepo;
        this.cmptOgnzRepo = cmptOgnzRepo;
        this.cmptPswdPlcyRepo = cmptPswdPlcyRepo;
        this.cmptUserLgnHstryRepo = cmptUserLgnHstryRepo;
        this.envCache = cacheManager.getCache(CacheType.ENV_INFO.getCacheNameWithCompany());
        this.sessionUserCache = cacheManager.getCache(CacheType.SESSION_USER_INFO.getCacheNameWithCompany());
    }


    private static final String ALL_ENV = "ALL_ENV";

    /**
     * 환경 정보 조회
     */
    @Autowired
    private CmptSysCrtrRepo cmptSysCrtrRepo;

    public CmptSysCrtrEntity getSysCrtrInfo(String sysCrtrCd) {
        if( envCache.get(sysCrtrCd) != null) {
            return envCache.get(sysCrtrCd, CmptSysCrtrEntity.class);
        }

        CmptSysCrtrEntity cmptSysCrtrEntity = cmptSysCrtrRepo.findById(sysCrtrCd).orElse(null);
        if( cmptSysCrtrEntity == null) {
            log.error("환경 정보가 존재하지 않습니다. {}", sysCrtrCd);
            return null;
        }

        envCache.put(sysCrtrCd, cmptSysCrtrEntity);
        return cmptSysCrtrEntity;
    }

    private static final String CMPT_PSWD_PLCY_ENTITY = "_cmptPswdPlcyEntity";

    public CmptPswdPlcyEntity getPswdPlcy() {
        if( envCache.get(CMPT_PSWD_PLCY_ENTITY) != null) {
            return envCache.get(CMPT_PSWD_PLCY_ENTITY, CmptPswdPlcyEntity.class);
        }
        
        List<CmptPswdPlcyEntity> cmptPswdPlcyEntityList = cmptPswdPlcyRepo.findAll();
        // 데이터가 존재하지 않을 경우 기본값 설정 . 이것은 오류
        if( cmptPswdPlcyEntityList.isEmpty()) {
            log.error("비밀번호 정책 데이터가 존재하지 않습니다.");
            return CmptPswdPlcyEntity.builder()
            .lgnTryPsbltyCnt(BigDecimal.valueOf(5))
            .build();
        }

        CmptPswdPlcyEntity cmptPswdPlcyEntity = cmptPswdPlcyEntityList.get(0);
        envCache.put(CMPT_PSWD_PLCY_ENTITY, cmptPswdPlcyEntity);
        return cmptPswdPlcyEntity;
    }

    public long selectLastUserConnectTime(String userId) {
        return commonUserAuthMapper.selectLastUserConnectTime(userId);
    }

    @Transactional
    public void updateUserLastConnectTime(String userId) {
        commonUserAuthMapper.updateUserLastConnectTime(userId);
    }



    /**
     * 세션 사용자 캐시 삭제
     * @param userId
     */
    public void clearSessionUserCache(String userId) {
        sessionUserCache.evict(userId);
    }


    /**
     * SecUser 정보 조회
     * @param userId
     * @return
     */
    public SecUser getSecUser(String userId) {

        if( sessionUserCache.get(userId) != null) {
            return sessionUserCache.get(userId, SecUser.class);
        }
		
        Optional<CmptUserEntity> optionalUserEntity = cmptUserRepo.findById(userId);
		if(!optionalUserEntity.isPresent()) {
            throw new RuntimeException("사용자 정보가 존재하지 않습니다. "+ userId);
		}

        CmptUserEntity userEntity = optionalUserEntity.get();
        if( StringUtils.isEmpty(userEntity.getAuthrtCd())) {
            userEntity.setAuthrtCd("ZZ");
        }

		SecUser secUser = new SecUser();
        BeanUtils.copyProperties(userEntity, secUser);
        
        // 권한 속성 추가
        Optional<CmptAuthrtEntity> authrtEntity = cmptAuthrtRepo.findById(userEntity.getAuthrtCd());
        if (!authrtEntity.isPresent()) {
            secUser.setOgnzInqRangCd("XX"); // 조직 문의 범위 코드 권한없음
            secUser.setAuthrtSe("0"); // 권한 레벨 없음
        } else {
            secUser.setOgnzInqRangCd(authrtEntity.get().getOgnzInqRangCd());
            secUser.setAuthrtSe(authrtEntity.get().getAuthrtSe());
        }

        sessionUserCache.put(userId, secUser);

        return secUser;
    }



    /**
     * 로그인 사용자 정보를 조회한다.
     * @param userEntity
     * @return
     */
    public LoginUserDto getLoginUserInfo(SecUser secUser) {
        
        LoginUserDto loginUserDto = new LoginUserDto();
        BeanUtils.copyProperties(secUser, loginUserDto);

        
        // 권한 속성 추가
        Optional<CmptAuthrtEntity> authrtEntity = cmptAuthrtRepo.findById(secUser.getAuthrtCd());
        if (!authrtEntity.isPresent()) {
            loginUserDto.setOgnzInqRangCd("XX"); // 조직 문의 범위 코드 권한없음
            loginUserDto.setBscScrnMenuId(""); // 기본 화면 메뉴 아이디 없음
        } else {
            loginUserDto.setBscScrnMenuId(authrtEntity.get().getBscScrnMenuId());
            loginUserDto.setOgnzInqRangCd(authrtEntity.get().getOgnzInqRangCd());
        }

		Optional<CmptOgnzEntity> ognzEntity = cmptOgnzRepo.findById(secUser.getOgnzCd());
		if (ognzEntity.isPresent()) {
			loginUserDto.setOgnzNm(ognzEntity.get().getOgnzNm());
		}


        return loginUserDto;
    }



    public Long getSessionTimeout() {
        return svcEnvConfig.getSessionTimeout() * 60 * 1000;
    }


    @Transactional
    public void autoLogout() {
        commonUserAuthMapper.selectUserLogoutList().stream().forEach(userId -> {
            userLogoutInternal(userId);
        });
    }


    @Transactional
    public void userLogout(String userId) {
        userLogoutInternal(userId);
    }

    private void userLogoutInternal(String userId) {
        if( StringUtils.isEmpty(userId)) {
            log.error("로그아웃 사용자 정보가 존재하지 않습니다.");
            return;
        }

        cmptUserRepo.findById(userId).ifPresent(userEntity -> {
            userEntity.setLgnYn("N");
            cmptUserRepo.save(userEntity);
        });

        // 사용자 로그아웃 시간 변경
        commonUserAuthMapper.updateUserLogoutTime(userId);
    }


    /**
     * 중복 로그인 상태 업데이트
     * @param statusMessage
     * @return
     */
    @Transactional
    public String updateDuplicationLoginStatus(String statusMessage) {
        String userId = getDuplicationLogoutUserId(statusMessage);
        if( userId != null) {
            userLogoutInternal(userId);
        }
        return "ok";
    }

    public String getDuplicationLogoutUserId(String statusMessage) {
        log.info("중복 로그인 상태 업데이트 데이터: [{}]", statusMessage);

        // 접속 사용자 아이피 조회
        String remoteIp = CoreUtils.webutils.getRemoteIp();

        String decryptedStatusMessage = SecurityMaskingUtil.decryptSimple(statusMessage);
        if( decryptedStatusMessage == null || decryptedStatusMessage.equals(statusMessage)) {
            log.error("중복 로그인 상태 업데이트 복호화 오류 remoteIp[{}], statusMessage[{}]", remoteIp, statusMessage);
            throw new InvalidationException("중복 로그인 상태 업데이트 파라미터 오류");
        }
        
        Map<String, Object> statusMessageMap = null;
        try{
            statusMessageMap = CoreUtils.toMapFromJson(decryptedStatusMessage);
        } catch( JsonProcessingException e) {
            log.error("중복 로그인 상태 업데이트 Map 전환 오류 remoteIp[{}], statusMessage[{}], decryptedStatusMessage[{}]", remoteIp, statusMessage, decryptedStatusMessage);
            throw new InvalidationException("중복 로그인 상태 업데이트 데이터 오류");
        }
        
        // 중복 해제 문자열 체크
        Long storedTime = MapUtils.getLongValue(statusMessageMap, "timestamp");
        String ip = MapUtils.getString(statusMessageMap, "ip");
        String userIp = MapUtils.getString(statusMessageMap, "userIp");
        String userId = MapUtils.getString(statusMessageMap, "userId");
        if( storedTime == null || ip == null || userIp == null || userId == null) {
            log.error("중복 로그인 상태 업데이트 값 오류 remoteIp[{}], statusMessage[{}], statusMessageMap[{}]", remoteIp, statusMessage, statusMessageMap);
            throw new InvalidationException("중복 로그인 상태 업데이트 오류");
        }

        long currentTime = System.currentTimeMillis();
        long difference = currentTime - storedTime;
        if( difference > 60000) {
            log.info("중복 로그인 상태 업데이트 시간[{}] 경과 remoteIp[{}], statusMessageMap[{}]", difference, remoteIp, statusMessageMap);
            throw new InvalidationException( "상태 변경 가능 시간이 경과하였습니다. 다시 로그인하세요");
        }

        if( remoteIp != null && !remoteIp.equals(userIp)) {
            log.error("중복 로그인 상태 업데이트 아이피 [{}]가 다름. 잘못된 접근 시도 remoteIp[{}], statusMessageMap[{}]", remoteIp, statusMessageMap);
            throw new InvalidationException("중복 로그인 상태 업데이트 실패(잘못된 접근.)");
        }

        CmptUserEntity userEntity = cmptUserRepo.findById(userId).orElse(null);
        if( userEntity == null) {
            log.error("중복 로그인 상태 업데이트 사용자 정보 없음 remoteIp[{}], statusMessageMap[{}]", remoteIp, statusMessageMap);
            throw new InvalidationException("중복 로그인 상태 업데이트 실패(사용자 정보 없음)");
        }

        if( !ip.equals(userEntity.getLgnIp())) {
            log.error("중복 로그인 상태 업데이트 아이피 [{}:{}]가 다름. 잘못된 접근 시도 remoteIp[{}], statusMessageMap[{}]", userEntity.getLgnIp(), ip, remoteIp, statusMessageMap);
            throw new InvalidationException("중복 로그인 상태 업데이트 실패(잘못된 접근!)");
        }

        if(!"Y".equals(userEntity.getLgnYn())) {
            log.info("이미 로그아웃 상태 [{}]", statusMessageMap);
            return null;
        }
        return userId;
    }


    /**
     * 근태현황 등록
     * @description 당일 최초 로그인 시 근태현황 테이블에 등록
     * @param userId
     */
    @Transactional
    public void updateWorkState(String userId){
        // 당일 근태관리 사용자 등록여부 확인
        // 사용자 정보 테이블에서 사용자 근무 시간 컬럼 확인
        // 없을 경우 0900
        log.debug("근태현황 등록 사용자 정보 userId[{}]", userId);

        // [V85] 검사 후 삽입을 없앴다. 같은 사용자의 로그인 요청이 겹치면 둘 다 「없음」을 보고
        // 둘 다 넣어 PK 충돌로 500 이 났다. 검사와 삽입 사이의 시간은 검사를 정교하게 해도
        // 사라지지 않는다 — 매퍼가 ON CONFLICT DO NOTHING 으로 한 문장에 끝낸다.
        // 이미 있으면 아무 일도 일어나지 않는다(첫 로그인 시각이 근태 판정의 근거다).
        commonUserAuthMapper.insertWorkList(userId);
    }

    public String selectUserHasLapsedLastLogin(String userId) {
        return commonUserAuthMapper.selectUserHasLapsedLastLogin(userId);
    }

}
