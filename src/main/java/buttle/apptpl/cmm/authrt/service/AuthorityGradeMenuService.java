package buttle.apptpl.cmm.authrt.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.cmm.authrt.dto.SYS3020001Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3020002Dto;
import buttle.apptpl.cmm.authrt.dto.SYS3020003Dto;
import buttle.apptpl.cmm.authrt.mapper.AuthorityGradeMenuMapper;
import buttle.apptpl.cmm.authrt.vo.SYS3020001Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3020002Vo;
import buttle.apptpl.cmm.authrt.vo.SYS3020003Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptMenuAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptMenuAuthrtPkey;
import buttle.apptpl.common.model.cmpt.repository.CmptMenuAuthrtRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptMenuAuthrtHstryRepo;
import buttle.apptpl.common.util.ModelConveter;

/**
 * 사용자등급별권한관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthorityGradeMenuService {

    private final AuthorityGradeMenuMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptMenuAuthrtRepo cmptMenuAuthrtRepo;

    /**
     * 사용자 권한 등급 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3020002Dto> selectUserAuthListList(SYS3020001Dto dto) {
        SYS3020001Vo vo = modelConveter.map(dto, SYS3020001Vo.class);
        List<SYS3020002Vo> resultList = mapper.selectUserAuthListList(vo);
        return modelConveter.mapList(resultList, SYS3020002Dto.class);
    }

    /**
     * 사용자 권한 등급 별 메뉴목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3020003Dto> selectUserAuthTypeMenuList(SYS3020001Dto dto) {
        SYS3020001Vo vo = modelConveter.map(dto, SYS3020001Vo.class);
        List<SYS3020003Vo> resultList = mapper.selectUserAuthTypeMenuList(vo);
        return modelConveter.mapList(resultList, SYS3020003Dto.class);
    }

    /**
     * 사용자 권한 등급 별 메뉴목록 저장
     */
    public Integer updateUserAuthTypeMenuList(List<SYS3020003Dto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new InvalidationException("저장할 메뉴 권한 데이터가 없습니다.");
        }

        // 권한 코드 추출 (모든 DTO에서 동일한 권한 코드 사용)
        String authrtCd = dtoList.get(0).getAuthrtCd();
        
        System.out.println("=== SYS3020U01 저장 시작 ===");
        System.out.println("권한코드: " + authrtCd);
        System.out.println("전달받은 데이터 수: " + dtoList.size());
        
        // 전달받은 데이터 상세 로깅
        for (int i = 0; i < dtoList.size(); i++) {
            SYS3020003Dto dto = dtoList.get(i);
            System.out.println("[" + (i+1) + "] 메뉴ID: " + dto.getMenuId() + 
                             ", 메뉴명: " + dto.getMenuNm() + 
                             ", 저장여부: " + dto.getSaveSeYn() + 
                             ", 권한코드: " + dto.getAuthrtCd());
        }

        int saveCount = 0;
        int deleteCount = 0;
        
        for (SYS3020003Dto dto : dtoList) {
            // 필수 필드 검증
            if (ObjectUtils.isEmpty(dto.getMenuId())) {
                throw new InvalidationException("메뉴 ID를 입력하세요.");
            }
            if (ObjectUtils.isEmpty(dto.getAuthrtCd())) {
                throw new InvalidationException("권한 코드를 입력하세요.");
            }
            if (ObjectUtils.isEmpty(dto.getSaveSeYn())) {
                throw new InvalidationException("저장 구분 여부를 입력하세요.");
            }

            // 복합키 생성
            CmptMenuAuthrtPkey pkey = new CmptMenuAuthrtPkey(dto.getMenuId(), dto.getAuthrtCd());
            
            if ("Y".equals(dto.getSaveSeYn())) {
                // 등록,수정
                CmptMenuAuthrtEntity entity = new CmptMenuAuthrtEntity();
                entity.setId(pkey);
                cmptMenuAuthrtRepo.save(entity);
                saveCount++;
                System.out.println("✅ 권한 부여: " + dto.getMenuId() + " (" + dto.getMenuNm() + ")");
            } else {
                // 삭제
                cmptMenuAuthrtRepo.deleteById(pkey);
                deleteCount++;
                System.out.println("❌ 권한 삭제: " + dto.getMenuId() + " (" + dto.getMenuNm() + ")");
            }
        }
        
        System.out.println("=== 데이터베이스 저장 결과 ===");
        System.out.println("권한 부여 건수: " + saveCount);
        System.out.println("권한 삭제 건수: " + deleteCount);
        System.out.println("총 처리 건수: " + (saveCount + deleteCount));

        return dtoList.size();
    }

  
} 