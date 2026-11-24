package buttle.apptpl.cmm.authrt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.cmm.authrt.dto.SYS3030001Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3030002Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3030003Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3030004Dto;
import buttle.apptpl.cmm.authrt.mapper.UserAuthorityMapper;
import buttle.apptpl.cmm.authrt.vo.SYS3030001Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3030002Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3030003Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3030004Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptUserMenuAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserMenuAuthrtPkey;
import buttle.apptpl.common.model.cmpt.entity.CmptUserPrgrmAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserPrgrmAuthrtPkey;
import buttle.apptpl.common.model.cmpt.entity.CmptUserMenuAuthrtHstryEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserPrgrmAuthrtHstryEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptUserMenuAuthrtRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptUserPrgrmAuthrtRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptUserMenuAuthrtHstryRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptUserPrgrmAuthrtHstryRepo;
import buttle.apptpl.common.util.ModelConveter;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import buttle.apptpl.common.util.AuthOgnzUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 사용자별권한관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthorityService {

    private final UserAuthorityMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptUserMenuAuthrtRepo cmptUserMenuAuthrtRepo;
    private final CmptUserPrgrmAuthrtRepo cmptUserPrgrmAuthrtRepo;
    private final CmptUserMenuAuthrtHstryRepo cmptUserMenuAuthrtHstryRepo;
    private final CmptUserPrgrmAuthrtHstryRepo cmptUserPrgrmAuthrtHstryRepo;
    private static final Logger logger =LoggerFactory.getLogger(UserAuthorityService.class);

    /**
     * 사용자 별 권한관리 대상 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3030002Dto> selectUserTypeAuthMgmtUserList(SYS3030001Dto dto) {
        SYS3030001Vo vo = modelConveter.map(dto, SYS3030001Vo.class);
        if(vo == null){
            throw new InvalidationException("전달된 값이 올바르지 않습니다.");
        }
        vo.setOgnzCd(AuthOgnzUserUtils.getAuthOgnzCd(vo.getOgnzCd()));
        vo.setUserId(AuthOgnzUserUtils.getAuthUserId(vo.getUserId()));
        List<SYS3030002Vo> resultList = mapper.selectUserTypeAuthMgmtUserList(vo);
        return modelConveter.mapList(resultList, SYS3030002Dto.class);
    }

    /**
     * 사용자별 메뉴 권한 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3030003Dto> selectUserMenuAuthList(SYS3030001Dto dto) {
        SYS3030001Vo vo = modelConveter.map(dto, SYS3030001Vo.class);
        List<SYS3030003Vo> resultList = mapper.selectUserTypePgmList(vo);
        return modelConveter.mapList(resultList, SYS3030003Dto.class);
    }
    
    /**
     * 사용자별 예외 메뉴 권한 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3030003Dto> selectUserTypeExcpPgmList(SYS3030001Dto dto) {
        SYS3030001Vo vo = modelConveter.map(dto, SYS3030001Vo.class);
        List<SYS3030003Vo> resultList = mapper.selectUserTypeExcpPgmList(vo);
        return modelConveter.mapList(resultList, SYS3030003Dto.class);
    }

    /**
     * 전체 메뉴 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3030003Dto> selectAllMenuList(SYS3030001Dto dto) {
        SYS3030001Vo vo = modelConveter.map(dto, SYS3030001Vo.class);
        List<SYS3030003Vo> resultList = mapper.selectAllMenuList(vo);
        return modelConveter.mapList(resultList, SYS3030003Dto.class);
    }

    /**
     * 전체 프로그램 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3030004Dto> selectAllProgramList(SYS3030001Dto dto) {
        SYS3030001Vo vo = modelConveter.map(dto, SYS3030001Vo.class);
        List<SYS3030004Vo> resultList = mapper.selectAllProgramList(vo);
        return modelConveter.mapList(resultList, SYS3030004Dto.class);
    }

    /**
     * 사용자별 프로그램 권한 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3030004Dto> selectUserProgramAuthList(SYS3030001Dto dto) {
        SYS3030001Vo vo = modelConveter.map(dto, SYS3030001Vo.class);
        List<SYS3030004Vo> resultList = mapper.selectUserProgramAuthList(vo);
        return modelConveter.mapList(resultList, SYS3030004Dto.class);
    }

    /**
     * 사용자별 메뉴 권한 저장
     */
    public Integer updateUserTypePgmAuth(List<SYS3030003Dto> dtoList) {
    	if (CollectionUtils.isEmpty(dtoList)) {
            throw new InvalidationException("삭제할 프로그램 권한 데이터가 없습니다.");
        }
        
        // 사용자 ID 추출 (모든 DTO에서 동일한 사용자 ID 사용)
        String userId = dtoList.get(0).getUserId();
        
        for (SYS3030003Dto dto : dtoList) {
	        if (ObjectUtils.isEmpty(dto.getUserId())) {
	            throw new InvalidationException("사용자 ID는 필수입니다.");
	        }
	        if (ObjectUtils.isEmpty(dto.getMenuId())) {
	            throw new InvalidationException("메뉴 ID는 필수입니다.");
	        }
	        CmptUserMenuAuthrtPkey pkey = new CmptUserMenuAuthrtPkey(dto.getMenuId(), dto.getUserId());
	        
	        Optional<CmptUserMenuAuthrtEntity> optEntity = cmptUserMenuAuthrtRepo.findById(pkey);

	        CmptUserMenuAuthrtEntity entity;
	        if (optEntity.isPresent()) {
	            entity = optEntity.get();
	            BeanUtils.copyProperties(dto, entity, "menuId", "userId");
	        } else {
	            entity = new CmptUserMenuAuthrtEntity();
	            BeanUtils.copyProperties(dto, entity);
	            entity.setId(pkey);
	        }
	        
	        cmptUserMenuAuthrtRepo.save(entity);
        }
        
        // 저장된 데이터를 즉시 데이터베이스에 반영
        cmptUserMenuAuthrtRepo.flush();
        
        // 저장 완료 후 실제 데이터베이스에서 사용자별 메뉴권한목록을 조회해서 히스토리에 저장
        saveUserMenuAuthHistory(userId);
        
        return dtoList.size();
    }

    /**
     * 사용자별 메뉴 권한 삭제
     */
    public Integer deleteUserTypePgmAuth(List<SYS3030003Dto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new InvalidationException("삭제할 프로그램 권한 데이터가 없습니다.");
        }
        
        // 사용자 ID 추출 (모든 DTO에서 동일한 사용자 ID 사용)
        String userId = dtoList.get(0).getUserId();
        
        for (SYS3030003Dto dto : dtoList) {
            if (ObjectUtils.isEmpty(dto.getUserId())) {
                throw new InvalidationException("사용자 ID는 필수입니다.");
            }
            if (ObjectUtils.isEmpty(dto.getMenuId())) {
                throw new InvalidationException("메뉴 ID는 필수입니다.");
            }
            CmptUserMenuAuthrtPkey pkey = new CmptUserMenuAuthrtPkey(dto.getMenuId(), dto.getUserId());
            
            if (!cmptUserMenuAuthrtRepo.existsById(pkey)) {
                throw new InvalidationException(String.format("삭제할 데이터가 존재하지 않습니다. (사용자:%s, 메뉴:%s)", dto.getUserId(), dto.getMenuId()));
            }

            cmptUserMenuAuthrtRepo.deleteById(pkey);
        }
        
        // 저장된 데이터를 즉시 데이터베이스에 반영
        cmptUserMenuAuthrtRepo.flush();

        saveUserMenuAuthHistory(userId);

        
        return dtoList.size();
    }

    /**
     * 사용자별 프로그램 권한 저장
     */
    public Integer updateUserTypeProgramAuth(List<SYS3030004Dto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new InvalidationException("저장할 프로그램 권한 데이터가 없습니다.");
        }
        
        // 사용자 ID 추출 (모든 DTO에서 동일한 사용자 ID 사용)
        String userId = dtoList.get(0).getUserId();
        
        for (SYS3030004Dto dto : dtoList) {
            if (ObjectUtils.isEmpty(dto.getUserId())) {
                throw new InvalidationException("사용자 ID는 필수입니다.");
            }
            if (ObjectUtils.isEmpty(dto.getPrgrmId())) {
                throw new InvalidationException("프로그램 ID는 필수입니다.");
            }
            CmptUserPrgrmAuthrtPkey pkey = new CmptUserPrgrmAuthrtPkey(dto.getUserId(), dto.getPrgrmId());
            
            Optional<CmptUserPrgrmAuthrtEntity> optEntity = cmptUserPrgrmAuthrtRepo.findById(pkey);

            CmptUserPrgrmAuthrtEntity entity;
            if (optEntity.isPresent()) {
                entity = optEntity.get();
                BeanUtils.copyProperties(dto, entity, "prgrmId", "userId");
            } else {
                entity = new CmptUserPrgrmAuthrtEntity();
                BeanUtils.copyProperties(dto, entity);
                entity.setId(pkey);
            }
            
            cmptUserPrgrmAuthrtRepo.save(entity);
        }
        
        // 저장된 데이터를 즉시 데이터베이스에 반영
        cmptUserPrgrmAuthrtRepo.flush();
        
        // 저장 완료 후 실제 데이터베이스에서 사용자별 프로그램권한목록을 조회해서 히스토리에 저장
        saveUserPrgrmAuthHistory(userId);
        
        return dtoList.size();
    }

    /**
     * 사용자별 프로그램 권한 삭제
     */
    public Integer deleteUserTypeProgramAuth(List<SYS3030004Dto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new InvalidationException("삭제할 프로그램 권한 데이터가 없습니다.");
        }
        
        // 사용자 ID 추출 (모든 DTO에서 동일한 사용자 ID 사용)
        String userId = dtoList.get(0).getUserId();
        
        for (SYS3030004Dto dto : dtoList) {
            if (ObjectUtils.isEmpty(dto.getUserId())) {
                throw new InvalidationException("사용자 ID는 필수입니다.");
            }
            if (ObjectUtils.isEmpty(dto.getPrgrmId())) {
                throw new InvalidationException("프로그램 ID는 필수입니다.");
            }
            CmptUserPrgrmAuthrtPkey pkey = new CmptUserPrgrmAuthrtPkey(dto.getUserId(), dto.getPrgrmId());
            
            if (!cmptUserPrgrmAuthrtRepo.existsById(pkey)) {
                throw new InvalidationException(String.format("삭제할 데이터가 존재하지 않습니다. (사용자:%s, 프로그램:%s)", dto.getUserId(), dto.getPrgrmId()));
            }

            cmptUserPrgrmAuthrtRepo.deleteById(pkey);
        }
        
        // 저장된 데이터를 즉시 데이터베이스에 반영
        cmptUserPrgrmAuthrtRepo.flush();
        
        // 삭제 완료 후 실제 데이터베이스에서 사용자별 프로그램권한목록을 조회해서 히스토리에 저장
         saveUserPrgrmAuthHistory(userId);

        
        return dtoList.size();
    }

    /**
     * 사용자별 메뉴 권한 히스토리 저장
     */
    private void saveUserMenuAuthHistory(String userId) {
            // 현재 사용자 정보 가져오기
            String currentUserId = SecurityUtils.getCurrentUserId();
            String currentOrgCd = SecurityUtils.getCurrentUser().getOgnzCd();
            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
              
            // 저장 완료 후 실제 데이터베이스에서 사용자별 메뉴권한목록 조회
            SYS3030001Dto searchDto = new SYS3030001Dto();
            searchDto.setUserId(userId);
            List<SYS3030003Dto> userMenuAuthList = selectUserMenuAuthList(searchDto);
            // 사용자별 메뉴 권한 목록을 히스토리에 저장
            for (SYS3030003Dto userMenuAuth : userMenuAuthList) {
                CmptUserMenuAuthrtHstryEntity historyEntity = CmptUserMenuAuthrtHstryEntity.builder()
                    .prcrId(currentUserId)
                    .prcsOgnzCd(currentOrgCd != null ? currentOrgCd : "DEFAULT")
                    .prcsDt(currentDateTime)
                    .userId(userId)
                    .menuId(userMenuAuth.getMenuId())
                    .authrtGrntSeCd("Y") // 권한 부여 상태
                    .build();
                     cmptUserMenuAuthrtHstryRepo.save(historyEntity);
            }
    }

    /**
     * 사용자별 프로그램 권한 히스토리 저장
     */
    private void saveUserPrgrmAuthHistory(String userId) {
            // 현재 사용자 정보 가져오기
            String currentUserId = SecurityUtils.getCurrentUserId();
            String currentOrgCd = SecurityUtils.getCurrentUser().getOgnzCd();
            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            
            // 저장 완료 후 실제 데이터베이스에서 사용자별 프로그램권한목록 조회
            SYS3030001Dto searchDto = new SYS3030001Dto();
            searchDto.setUserId(userId);
            List<SYS3030004Dto> userPrgrmAuthList = selectUserProgramAuthList(searchDto);
            // 사용자별 프로그램 권한 목록을 히스토리에 저장
            for (SYS3030004Dto userPrgrmAuth : userPrgrmAuthList) {
                CmptUserPrgrmAuthrtHstryEntity historyEntity = CmptUserPrgrmAuthrtHstryEntity.builder()
                    .prcrId(currentUserId)
                    .prcsOgnzCd(currentOrgCd != null ? currentOrgCd : "DEFAULT")
                    .prcsDt(currentDateTime)
                    .userId(userId)
                    .prgrmId(userPrgrmAuth.getPrgrmId())
                    .authrtGrntSeCd("Y") // 권한 부여 상태
                    .build();
                    cmptUserPrgrmAuthrtHstryRepo.save(historyEntity);
            }
    }
} 