package buttle.apptpl.cmm.loginuser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.cmm.loginuser.dto.COM1100001Dto;
import buttle.apptpl.cmm.loginuser.dto.COM1100002Dto;
import buttle.apptpl.cmm.loginuser.mapper.LoginUserInfoMapper;
import buttle.apptpl.cmm.loginuser.vo.COM1100001Vo;
import buttle.apptpl.cmm.loginuser.vo.COM1100002Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptUserRepo;
import buttle.apptpl.common.util.ModelConveter;

import java.util.Optional;

/**
 * 로그인사용자정보 Service
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LoginUserInfoService {

    private final LoginUserInfoMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptUserRepo cmptUserRepo;

    @Value("${file.upload.max-size:5242880}")
    private long maxFileSize;

    @Value("${file.upload.allowed-extensions:jpg,jpeg,png,gif}")
    private String allowedExtensions;

    /**
     * 사용자 프로필 조회
     * @param dto 사용자 프로필 요청
     * @return 사용자 프로필 응답
     */
    @Transactional(readOnly = true)
    public COM1100001Dto selectUserProfile(COM1100001Dto dto) {
        COM1100001Vo vo = modelConveter.map(dto, COM1100001Vo.class);
        COM1100002Vo result = mapper.selectUserProfile(vo);
        return modelConveter.map(result, COM1100001Dto.class);
    }

    /**
     * 사용자 이미지 변경 (base64 데이터)
     * @param dto 사용자 이미지 정보 (base64 데이터 포함)
     * @return 사용자 프로필 응답
     */
    public COM1100001Dto updateUserImageBase64(COM1100002Dto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getUserId())) {
            throw new InvalidationException("사용자 ID를 입력하세요.");
        }
        if (ObjectUtils.isEmpty(dto.getUserPhoto())) {
            throw new InvalidationException("이미지 데이터를 입력하세요.");
        }

        // 사용자 존재 여부 확인
        Optional<CmptUserEntity> existingUserOpt = cmptUserRepo.findById(dto.getUserId());
        if (!existingUserOpt.isPresent()) {
            throw new InvalidationException("존재하지 않는 사용자입니다.");
        }

        // base64 이미지 데이터 검증
        validateBase64Image(dto.getUserPhoto());

        // 사용자 이미지 업데이트 (base64 데이터로 저장)
        CmptUserEntity userEntity = existingUserOpt.get();
        userEntity.setUserPhoto(dto.getUserPhoto());
        cmptUserRepo.save(userEntity);

        return modelConveter.map(userEntity, COM1100001Dto.class);
    }

    /**
     * base64 이미지 데이터 검증
     */
    private void validateBase64Image(String base64Data) {
        if (ObjectUtils.isEmpty(base64Data)) {
            throw new InvalidationException("이미지 데이터가 없습니다.");
        }

        // base64 형식 검증
        if (!base64Data.startsWith("data:image/")) {
            throw new InvalidationException("올바른 이미지 형식이 아닙니다.");
        }

        // 파일 크기 검증 (base64는 원본보다 약 33% 크므로 고려)
        String base64Content = base64Data.substring(base64Data.indexOf(",") + 1);
        int decodedSize = (int) (base64Content.length() * 0.75);
        if (decodedSize > maxFileSize) {
            throw new InvalidationException("이미지 크기가 너무 큽니다. 최대 " + (maxFileSize / 1024 / 1024) + "MB까지 업로드 가능합니다.");
        }

        // 이미지 형식 검증
        String mimeType = base64Data.substring(5, base64Data.indexOf(";"));
        String extension = mimeType.substring(mimeType.indexOf("/") + 1).toLowerCase();
        String[] allowedExts = allowedExtensions.split(",");
        boolean isValidExtension = false;
        
        for (String ext : allowedExts) {
            if (extension.equals(ext.trim())) {
                isValidExtension = true;
                break;
            }
        }
        
        if (!isValidExtension) {
            throw new InvalidationException("허용되지 않는 이미지 형식입니다. 허용된 형식: " + allowedExtensions);
        }
    }
}