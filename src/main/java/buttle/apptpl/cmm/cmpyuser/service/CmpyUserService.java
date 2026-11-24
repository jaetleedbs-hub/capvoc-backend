package buttle.apptpl.cmm.cmpyuser.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import buttle.apptpl.api.shared.dto.UserSearchReqDto;
import buttle.apptpl.api.shared.dto.UserListResDto;
import buttle.apptpl.api.shared.dto.UserDetailResDto;
import buttle.apptpl.api.shared.dto.UserSaveReqDto;
import buttle.apptpl.common.constant.AuditConstants;
import buttle.apptpl.cmm.cmpyuser.mapper.CmpyUserMapper;
import buttle.apptpl.api.shared.vo.UserSearchReqVo;
import buttle.apptpl.api.shared.vo.UserListResVo;
import buttle.apptpl.api.shared.vo.UserDetailResVo;
import buttle.apptpl.api.shared.vo.UserSaveReqVo;
import buttle.apptpl.api.shared.vo.AuthrtResVo;
import buttle.apptpl.api.shared.dto.AuthrtResDto;
import buttle.apptpl.common.util.ModelConveter;
import java.util.List;
import java.util.Random;

/**
 * BO401_S 사용자 관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CmpyUserService {
    
    private final CmpyUserMapper mapper;
    private final ModelConveter modelConveter;
    private final BCryptPasswordEncoder passwordEncoder;
    
    /**
     * 사용자 목록 조회
     * @param dto 검색 조건
     * @return 사용자 목록
     */
    public Page<UserListResDto> getUserList(UserSearchReqDto dto, Pageable pageable) {
        UserSearchReqVo vo = modelConveter.map(dto, UserSearchReqVo.class);
        Page<UserListResVo> resultList = mapper.getUserList(vo, pageable);

        return resultList.map(voItem -> modelConveter.map(voItem, UserListResDto.class));
    }
    
    /**
     * 사용자 상세 조회
     * @param userId 사용자ID
     * @return 사용자 상세 정보
     */
    public UserDetailResDto getUserDetail(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자ID가 필요합니다.");
        }
        
        UserDetailResVo vo = mapper.getUserDetail(userId);
        if (vo == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        
        return modelConveter.map(vo, UserDetailResDto.class);
    }
    
    /**
     * 사용자 정보 저장 (신규 등록 또는 수정)
     * @param dto 사용자 정보
     * @return 저장 건수
     */
    public int saveUser(UserSaveReqDto dto) {
        // 1. 필수값 검증
        validateRequiredFields(dto);
        
        // 2. DTO → VO 변환
        UserSaveReqVo vo = modelConveter.map(dto, UserSaveReqVo.class);
        
        // 3. 등록자/수정자 ID 설정
        // TODO: 세션에서 사용자 ID 가져와서 설정
        vo.setRgtrId(AuditConstants.SYSTEM);
        vo.setMdfrId(AuditConstants.SYSTEM);
        
        // 4. 신규 등록인지 수정인지 확인
        int duplicateCount = mapper.checkUserIdDuplicate(dto.getUserId());

        if (duplicateCount > 0) {
            return mapper.updateUser(vo);
        } else {
            vo.setPswd(passwordEncoder.encode(dto.getUserId()));
            return mapper.insertUser(vo);
        }
    }
    
    /**
     * 필수값 검증
     * @param dto 사용자 정보
     * @throws IllegalArgumentException 필수값이 누락된 경우
     */
    private void validateRequiredFields(UserSaveReqDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("사용자 정보가 필요합니다.");
        }
        
        // 사용자ID 검증 (필수, 50자 이내, 영문숫자만)
        if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자ID를 입력해 주세요.");
        }
        if (dto.getUserId().length() > 50) {
            throw new IllegalArgumentException("사용자ID는 50자 이내로 입력해 주세요.");
        }
        if (!dto.getUserId().matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("사용자ID는 영문과 숫자만 입력 가능합니다.");
        }
        
        // 사용자명 검증 (필수, 50자 이내)
        if (dto.getUserNm() == null || dto.getUserNm().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명을 입력해 주세요.");
        }
        if (dto.getUserNm().length() > 50) {
            throw new IllegalArgumentException("사용자명은 50자 이내로 입력해 주세요.");
        }
        
        // 사용자등급 검증 (필수)
        if (dto.getUserGradeCd() == null || dto.getUserGradeCd().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자등급을 선택해 주세요.");
        }
        
        // 사용자유형 검증 (필수)
        if (dto.getUserTypeCd() == null || dto.getUserTypeCd().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자유형을 선택해 주세요.");
        }
        
        // 소속조직 검증 (필수)
        if (dto.getOgnzCd() == null || dto.getOgnzCd().trim().isEmpty()) {
            throw new IllegalArgumentException("소속조직을 선택해 주세요.");
        }
        
        // 사용여부 검증 (필수)
        if (dto.getUseYn() == null || dto.getUseYn().trim().isEmpty()) {
            throw new IllegalArgumentException("사용여부를 선택해 주세요.");
        }
        
        // 휴대전화번호 검증 (선택, 숫자만, 11자 이내)
        if (dto.getMblTelNo() != null && !dto.getMblTelNo().trim().isEmpty()) {
            if (!dto.getMblTelNo().matches("^[0-9]+$")) {
                throw new IllegalArgumentException("휴대전화번호는 숫자만 입력 가능합니다.");
            }
            if (dto.getMblTelNo().length() > 11) {
                throw new IllegalArgumentException("휴대전화번호는 11자 이내로 입력해 주세요.");
            }
        }
        
        // 자택전화번호 검증 (선택, 숫자만, 11자 이내)
        if (dto.getHomeTelNo() != null && !dto.getHomeTelNo().trim().isEmpty()) {
            if (!dto.getHomeTelNo().matches("^[0-9]+$")) {
                throw new IllegalArgumentException("자택전화번호는 숫자만 입력 가능합니다.");
            }
            if (dto.getHomeTelNo().length() > 11) {
                throw new IllegalArgumentException("자택전화번호는 11자 이내로 입력해 주세요.");
            }
        }
        
        // 이메일 검증 (선택, 이메일 형식, 40자 이내)
        if (dto.getEmlAddr() != null && !dto.getEmlAddr().trim().isEmpty()) {
            if (!dto.getEmlAddr().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                throw new IllegalArgumentException("올바른 이메일 주소를 입력해 주세요.");
            }
            if (dto.getEmlAddr().length() > 40) {
                throw new IllegalArgumentException("이메일주소는 40자 이내로 입력해 주세요.");
            }
        }
        
        // 퇴사일 검증 (퇴사일은 입사일보다 이후여야 함)
        if (dto.getJncmpYmd() != null && dto.getRsgntnYmd() != null) {
            if (!dto.getJncmpYmd().trim().isEmpty() && !dto.getRsgntnYmd().trim().isEmpty()) {
                if (dto.getRsgntnYmd().compareTo(dto.getJncmpYmd()) < 0) {
                    throw new IllegalArgumentException("사용종료일은 사용시작일보다 이후여야 합니다.");
                }
            }
        }
    }
    
    /**
     * 로그인 정보 초기화
     * @param userId 사용자ID
     * @return 초기화 건수
     */
    public int resetUserLogin(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자ID가 필요합니다.");
        }
        
        int result = mapper.resetUserLogin(userId);
        if (result == 0) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        
        return result;
    }
    
    /**
     * 비밀번호 초기화 (임시 비밀번호 발급)
     * @param userId 사용자ID
     * @return 임시 비밀번호 (평문)
     */
    public String resetUserPassword(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자ID가 필요합니다.");
        }
        
        // 1. 임시 비밀번호 생성 (8자리 영문대소문자+숫자 조합)
        String tempPassword = generateTempPassword();
        
        // 2. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(tempPassword);
        
        // 3. 비밀번호 초기화 실행
        int result = mapper.resetUserPassword(userId, encryptedPassword);
        if (result == 0) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        
        // 4. 임시 비밀번호 반환 (평문)
        return tempPassword;
    }
    
    /**
     * 임시 비밀번호 생성 (8자리 영문대소문자+숫자 조합)
     * @return 임시 비밀번호
     */
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder tempPassword = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 8; i++) {
            tempPassword.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return tempPassword.toString();
    }

    public List<AuthrtResDto> getAuthrtList() {
        List<AuthrtResVo> resultList = mapper.getAuthrtList();
        return modelConveter.mapList(resultList, AuthrtResDto.class);
    }
}
