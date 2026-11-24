package buttle.apptpl.cmm.user.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.api.common.components.service.CommonUserAuthService;
import buttle.apptpl.cmm.user.dto.SYS0030001Dto;
import buttle.apptpl.cmm.user.dto.SYS0030002Dto;
import buttle.apptpl.cmm.user.dto.SYS0030003Dto;
import buttle.apptpl.cmm.user.mapper.UserMapper;
import buttle.apptpl.cmm.user.vo.SYS0030001Vo;
import buttle.apptpl.cmm.user.vo.SYS0030002Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserChgHstryEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserChgHstryPkey;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptAuthrtRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptOgnzRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptUserChgHstryRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptUserRepo;
import buttle.apptpl.common.security.bruteforce.LoginAttemptUtils;
import buttle.apptpl.common.security.crypto.CustomPasswordEncoder;
import buttle.apptpl.common.util.AuthOgnzUserUtils;
import buttle.apptpl.common.util.ModelConveter;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptUserRepo cmptUserRepo;
    private final CmptOgnzRepo cmptOgnzRepo;
    private final CmptAuthrtRepo cmptAuthrtRepo;
    private final CustomPasswordEncoder customPasswordEncoder;
    private final CmptUserChgHstryRepo cmptUserChgHstryRepo;
    private final CommonUserAuthService commonUserAuthService;
    private final LoginAttemptUtils loginAttemptUtils;

    /**
     * 사용자 정보 조회
     * @param dto 사용자관리 목록 요청
     * @return 사용자관리 목록 요청
     */
    @Transactional(readOnly = true)
    public List<SYS0030001Dto> selectUserInfoList(SYS0030001Dto dto) {
        SYS0030001Vo vo = modelConveter.map(dto, SYS0030001Vo.class);
        if( vo == null){
            throw new InvalidationException("전달된 값이 올바르지 않습니다.");
        }

        // 사용자 권한 코드에 따른 조직 조회 가능 여부 조회
        vo.setOgnzCd(AuthOgnzUserUtils.getAuthOgnzCd(vo.getOgnzCd()));
        vo.setUserId(AuthOgnzUserUtils.getAuthUserId(vo.getUserId()));

        return modelConveter.mapList(mapper.selectUserInfoList(vo), SYS0030001Dto.class);
    }

    /**
     * 사용자 상세 정보 조회
     * @param dto 사용자정보 상세 요청 및 응답
     * @return 사용자관리 목록 요청
     */
    @Transactional(readOnly = true)
    public SYS0030002Dto selectUserDtlInfo(SYS0030002Dto dto) {
        SYS0030002Vo vo = modelConveter.map(dto, SYS0030002Vo.class);
        if( vo == null){
            throw new InvalidationException("전달된 값이 올바르지 않습니다.");
        }

        vo.setUserId(AuthOgnzUserUtils.getAuthUserId(vo.getUserId()));

        return modelConveter.map(mapper.selectUserDtlInfo(vo), SYS0030002Dto.class);
    }

    /**
     * 사용자정보 저장
     * @param dto 사용자정보 상세 요청 및 응답
     * @return 사용자정보 상세 요청 및 응답
     */
    public SYS0030002Dto updateUserInfo(SYS0030002Dto dto) {
        if(dto == null){
            throw new InvalidationException("요청한 데이터가 없습니다.");
        }

        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getUserId())) {
            throw new InvalidationException("사용자 ID를 입력하세요.");
        }


        // 조직 코드 존재 여부 확인
        if (!ObjectUtils.isEmpty(dto.getOgnzCd()) && !cmptOgnzRepo.existsById(dto.getOgnzCd())) {
            throw new InvalidationException("존재하지 않는 조직 코드입니다.");
        }

        // 사용자ID (비어있지 않을 때만 검증)
        if (!ObjectUtils.isEmpty(dto.getUserId()) && !dto.getUserId().matches("^[a-zA-Z0-9]+$")) {
            throw new InvalidationException("사용자ID는 영문과 숫자만 사용할 수 있습니다.");
        }

        // 전화번호 입력 숫자만 (비어있지 않을 때만 검증)
        if (!ObjectUtils.isEmpty(dto.getHomeTelNo()) && !dto.getHomeTelNo().matches("^[0-9]+$")) {
            throw new InvalidationException("자택전화번호는 숫자만 사용할 수 있습니다.");
        }
        if (!ObjectUtils.isEmpty(dto.getMblTelNo()) && !dto.getMblTelNo().matches("^[0-9]+$")) {
            throw new InvalidationException("휴대전화번호는 숫자만 사용할 수 있습니다.");
        }
        if (!ObjectUtils.isEmpty(dto.getExtNo()) && !dto.getExtNo().matches("^[0-9]+$")) {
            throw new InvalidationException("내선번호는 숫자만 사용할 수 있습니다.");
        }

        // 이메일 주소 입력 이메일 형식 (비어있지 않을 때만 검증)
        if (!ObjectUtils.isEmpty(dto.getEmlAddr()) && !dto.getEmlAddr().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidationException("이메일 주소 형식이 올바르지 않습니다.");
        }
        
        dto.setOgnzCd(AuthOgnzUserUtils.getAuthOgnzCd(dto.getOgnzCd()));

        // 권한 코드 검증 및 권한 레벨 확인
        if (!ObjectUtils.isEmpty(dto.getAuthrtCd())) {
            // 현재 사용자 권한 코드 조회
            String currentUserAuthrtCd = SecurityUtils.getCurrentUser().getAuthrtCd();
            if (ObjectUtils.isEmpty(currentUserAuthrtCd)) {
                throw new InvalidationException("현재 사용자의 권한 코드를 확인할 수 없습니다.");
            }
            
            // 현재 사용자 권한 구분 조회
            CmptAuthrtEntity currentAuthrtEntity = cmptAuthrtRepo.findById(currentUserAuthrtCd)
                .orElseThrow(() -> new InvalidationException("현재 사용자의 권한 코드가 존재하지 않습니다."));
        
            CmptAuthrtEntity targetAuthrtEntity = cmptAuthrtRepo.findById(dto.getAuthrtCd())
                .orElseThrow(() -> new InvalidationException("존재하지 않는 권한 코드입니다."));
            
            try {
                int currentUserAuthrtSe = Integer.parseInt(currentAuthrtEntity.getAuthrtSe());
                int targetAuthrtSe = Integer.parseInt(targetAuthrtEntity.getAuthrtSe());
                
                if (currentUserAuthrtSe < targetAuthrtSe) {
                    throw new InvalidationException("권한이 없습니다.");
                }
            } catch (NumberFormatException e) {
                throw new InvalidationException("권한 순서 정보가 올바르지 않습니다.");
            }
        }

        // 기존 사용자 확인
        Optional<CmptUserEntity> existingUserOpt = cmptUserRepo.findById(dto.getUserId());
        CmptUserEntity userEntity;
        
        if (existingUserOpt.isPresent()) {
            userEntity = existingUserOpt.get();

            // 사용자가 존재할 경우에만 체크한다.
            dto.setUserId(AuthOgnzUserUtils.getAuthUserId(dto.getUserId()));
        
            // 변경 이력 저장 (각각 독립적으로 처리)
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            
            // 현재 로그인한 사용자 정보 (변경 수행자)
            String currentUserId = SecurityUtils.getCurrentUserId();
            String currentUserOgnzCd = SecurityUtils.getCurrentUserOgnzCd();
            
            
            // 조직 코드 변경 확인 및 이력 저장
            if (!Objects.equals(userEntity.getOgnzCd(), dto.getOgnzCd())) {
                CmptUserChgHstryEntity ognzChgHistory = new CmptUserChgHstryEntity();
                CmptUserChgHstryPkey ognzHistoryId = CmptUserChgHstryPkey.builder()
                    .userId(dto.getUserId())
                    .chgDt(currentTime)
                    .chgTypeCd("OGNZ")
                    .build();
                ognzChgHistory.setId(ognzHistoryId);
                ognzChgHistory.setChgBfrInfo(String.format(userEntity.getOgnzCd() != null ? userEntity.getOgnzCd() : ""));
                ognzChgHistory.setChgAftrInfo(String.format(dto.getOgnzCd() != null ? dto.getOgnzCd() : ""));
                ognzChgHistory.setPrcrId(currentUserId);
                ognzChgHistory.setPrcrOgnzCd(currentUserOgnzCd);
                ognzChgHistory.setPrcsDt(currentTime);
                cmptUserChgHstryRepo.save(ognzChgHistory);
            }
            
            // 권한 코드 변경 확인 및 이력 저장
            if (!Objects.equals(userEntity.getAuthrtCd(), dto.getAuthrtCd())) {
                CmptUserChgHstryEntity authrtChgHistory = new CmptUserChgHstryEntity();
                CmptUserChgHstryPkey authrtHistoryId = CmptUserChgHstryPkey.builder()
                    .userId(dto.getUserId())
                    .chgDt(currentTime)
                    .chgTypeCd("AUTHRT")
                    .build();
                authrtChgHistory.setId(authrtHistoryId);
                authrtChgHistory.setChgBfrInfo(String.format(userEntity.getAuthrtCd() != null ? userEntity.getAuthrtCd() : ""));
                authrtChgHistory.setChgAftrInfo(String.format(dto.getAuthrtCd() != null ? dto.getAuthrtCd() : ""));
                authrtChgHistory.setPrcrId(currentUserId);
                authrtChgHistory.setPrcrOgnzCd(currentUserOgnzCd);
                authrtChgHistory.setPrcsDt(currentTime);
                cmptUserChgHstryRepo.save(authrtChgHistory);
            }
            
            // ReadOnly 필드 보호
            BeanUtils.copyProperties(dto, userEntity, 
                "userId", 
                "lgnYn",       // 로그인 여부 (시스템 관리)
                "lgnTryCnt",   // 로그인 시도 수 (시스템 관리)
                "lgnIp",       // 로그인 IP (시스템 관리)
                "pswdChgDt",   // 비밀번호 변경일시 (시스템 관리)
                "pswd"         // 비밀번호 (별도 메서드로 관리)
            );
        } else {

            SecUser secUser = SecurityUtils.getCurrentUser();
            String ognzInqRangCd = secUser.getOgnzInqRangCd();
            if("03".equals( ognzInqRangCd)) {
                throw new InvalidationException("등록 권한이 없는 사용자입니다.");
            }

            // 신규 사용자 생성 시 중복 체크
            if (cmptUserRepo.existsById(dto.getUserId())) {
                throw new InvalidationException("이미 존재하는 사용자 ID입니다.");
            }
            
            userEntity = new CmptUserEntity();
            userEntity.setUserId(dto.getUserId());
            userEntity.setLgnYn("N");
            userEntity.setLgnTryCnt(0);
            userEntity.setPswd(customPasswordEncoder.encode(dto.getUserId()));  

            BeanUtils.copyProperties(dto, userEntity, "lgnTryCnt");
            

            // 기본값 설정
            if (ObjectUtils.isEmpty(userEntity.getUseYn())) {
                userEntity.setUseYn("Y");
            }
        }
        
        cmptUserRepo.save(userEntity);
        
        return modelConveter.map(userEntity, SYS0030002Dto.class);
    }

    /**
     * 로그인 초기화
     * @param dto 사용자관리 로그인 및 비밀번호 초기화
     * @return 사용자관리 로그인 및 비밀번호 초기화
     */
    public SYS0030003Dto resetLogin(SYS0030003Dto dto) {
        if(dto == null){
            throw new InvalidationException("요청한 데이터가 없습니다.");
        }

        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getUserId())) {
            throw new InvalidationException("사용자 ID를 입력하세요.");
        }

        // 사용자 존재 여부 확인
        Optional<CmptUserEntity> userOpt = cmptUserRepo.findById(dto.getUserId());
        if (!userOpt.isPresent()) {
            throw new InvalidationException("존재하지 않는 사용자입니다.");
        }

        dto.setUserId(AuthOgnzUserUtils.getAuthUserId(dto.getUserId()));


        CmptUserEntity userEntity = userOpt.get();
        int maxAttemptCnt = loginAttemptUtils.getLgnTryPsbltyCnt();
        // 로그인 관련 필드 초기화
        commonUserAuthService.userLogout(userEntity.getUserId());
        if(userEntity.getLgnTryCnt() >= maxAttemptCnt) {
            userEntity.setLgnTryCnt(0);
        }        
        cmptUserRepo.save(userEntity);
        return dto;
    }

    /**
     * 비밀번호 초기화
     * @param dto 사용자관리 로그인 및 비밀번호 초기화
     * @return 사용자관리 로그인 및 비밀번호 초기화
     */
    public SYS0030003Dto resetPwd(SYS0030003Dto dto) {
        if(dto == null){
            throw new InvalidationException("요청한 데이터가 없습니다.");
        }
        
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getUserId())) {
            throw new InvalidationException("사용자 ID를 입력하세요.");
        }

        // 사용자 존재 여부 확인
        Optional<CmptUserEntity> userOpt = cmptUserRepo.findById(dto.getUserId());
        if (!userOpt.isPresent()) {
            throw new InvalidationException("존재하지 않는 사용자입니다.");
        }

        dto.setUserId(AuthOgnzUserUtils.getAuthUserId(dto.getUserId()));

        CmptUserEntity userEntity = userOpt.get();
        // 비밀번호 초기화 (유저ID를 암호화하여 초기화)
        String encodedNewPassword = customPasswordEncoder.encode(dto.getUserId());
        userEntity.setPswd(encodedNewPassword);
       
        cmptUserRepo.save(userEntity);
        return dto;
    }

    /**
     * 내선번호만 업데이트
     * 
     * @param userId 사용자 ID
     * @param extNo 내선 번호
     * @return 업데이트된 사용자 정보
     */
    @Transactional
    public SYS0030002Dto updateUserExtNo(SYS0030002Dto dto) {
        if(dto == null){
            throw new InvalidationException("요청한 데이터가 없습니다.");
        }
        
        String userId = dto.getUserId();
        String extNo = dto.getExtNo();

        // 필수 필드 검증
        if (ObjectUtils.isEmpty(userId)) {
            throw new InvalidationException("사용자 ID를 입력하세요.");
        }
        
        // 내선번호 유효성 검증
        if (!ObjectUtils.isEmpty(extNo) && !extNo.matches("^[0-9]+$")) {
            throw new InvalidationException("내선번호는 숫자만 사용할 수 있습니다.");
        }
        
        // 사용자 존재 여부 확인
        CmptUserEntity userEntity = cmptUserRepo.findById(userId).orElseThrow(() -> new InvalidationException("존재하지 않는 사용자입니다."));
        
        // 권한 체크 (필요시)
        // dto.setUserId(AuthOgnzUserUtils.getAuthUserId(userId));
        
        // 내선번호만 업데이트
        userEntity.setExtNo(extNo);
        
        // 저장
        cmptUserRepo.save(userEntity);
        
        return modelConveter.map(userEntity, SYS0030002Dto.class);
    }
} 