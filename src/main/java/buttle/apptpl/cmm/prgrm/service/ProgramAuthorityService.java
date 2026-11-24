package buttle.apptpl.cmm.prgrm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.cmm.prgrm.dto.SYS3050001Dto;
import buttle.apptpl.cmm.prgrm.dto.SYS3050002Dto;
import buttle.apptpl.cmm.prgrm.dto.SYS3050003Dto;
import buttle.apptpl.cmm.prgrm.mapper.ProgramAuthorityMapper;
import buttle.apptpl.cmm.prgrm.vo.SYS3050001Vo;
import buttle.apptpl.cmm.prgrm.vo.SYS3050002Vo;
import buttle.apptpl.cmm.prgrm.vo.SYS3050003Vo;
import buttle.apptpl.common.model.cmpt.entity.CmptPrgrmAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptPrgrmAuthrtPkey;
import buttle.apptpl.common.model.cmpt.repository.CmptPrgrmAuthrtRepo;
import buttle.apptpl.common.util.ModelConveter;
import micoai.corebase.exception.InvalidationException;

import java.util.List;

/**
 * SYS3050 프로그램권한관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProgramAuthorityService {

    private final ProgramAuthorityMapper mapper;
    private final ModelConveter modelConveter;
    private final CmptPrgrmAuthrtRepo cmptPrgrmAuthrtEntityRepo;

    /**
     * 사용자 권한 등급 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3050002Dto> selectApAuthList(SYS3050001Dto dto) {
        SYS3050001Vo vo = modelConveter.map(dto, SYS3050001Vo.class);
        List<SYS3050002Vo> voList = mapper.selectUserAuthListList(vo);
        return modelConveter.mapList(voList, SYS3050002Dto.class);
    }

    /**
     * 사용자 권한 등급 별 프로그램 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SYS3050003Dto> selectUserAuthTypePrgrmList(SYS3050001Dto dto) {
        SYS3050001Vo vo = modelConveter.map(dto, SYS3050001Vo.class);
        List<SYS3050003Vo> voList = mapper.selectUserAuthTypePrgrmList(vo);
        return modelConveter.mapList(voList, SYS3050003Dto.class);
    }


    /**
     * API 권한 목록 저장
    * @param dtoList 프로그램권한 요청 목록
     * @return 처리 건수
     */
    public Integer updateUserAuthTypePrgrmList(List<SYS3050003Dto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new InvalidationException("저장할 프로그램 권한 데이터가 없습니다.");
        }

        // 권한 코드 추출 (모든 DTO에서 동일한 권한 코드 사용)
        String authrtCd = dtoList.get(0).getAuthrtCd();
        
        System.out.println("=== SYS3050U01 저장 시작 ===");
        System.out.println("권한코드: " + authrtCd);
        System.out.println("전달받은 데이터 수: " + dtoList.size());
        
        // 전달받은 데이터 상세 로깅
        for (int i = 0; i < dtoList.size(); i++) {
            SYS3050003Dto dto = dtoList.get(i);
            System.out.println("[" + (i+1) + "] 프로그램ID: " + dto.getPrgrmId() + 
                             ", 프로그램명: " + dto.getPrgrmNm() + 
                             ", 저장여부: " + dto.getSaveSeYn() + 
                             ", 권한코드: " + dto.getAuthrtCd());
        }

        int saveCount = 0;
        int deleteCount = 0;
        
        for (SYS3050003Dto dto : dtoList) {
            // 필수 필드 검증
            if (ObjectUtils.isEmpty(dto.getPrgrmId())) {
                throw new InvalidationException("프로그램 ID를 입력하세요.");
            }
            if (ObjectUtils.isEmpty(dto.getAuthrtCd())) {
                throw new InvalidationException("권한 코드를 입력하세요.");
            }
            if (ObjectUtils.isEmpty(dto.getSaveSeYn())) {
                throw new InvalidationException("저장 구분 여부를 입력하세요.");
            }

            // 복합키 생성
            CmptPrgrmAuthrtPkey pkey = new CmptPrgrmAuthrtPkey(dto.getPrgrmId(), dto.getAuthrtCd());
            
            if ("Y".equals(dto.getSaveSeYn())) {
                // 등록,수정
                CmptPrgrmAuthrtEntity entity = new CmptPrgrmAuthrtEntity();
                entity.setId(pkey);
                cmptPrgrmAuthrtEntityRepo.save(entity);
                saveCount++;
                System.out.println("✅ 권한 부여: " + dto.getPrgrmId() + " (" + dto.getPrgrmNm() + ")");
            } else {
                // 삭제
                cmptPrgrmAuthrtEntityRepo.deleteById(pkey);
                deleteCount++;
                System.out.println("❌ 권한 삭제: " + dto.getPrgrmId() + " (" + dto.getPrgrmNm() + ")");
            }
        }
        
        System.out.println("=== 데이터베이스 저장 결과 ===");
        System.out.println("권한 부여 건수: " + saveCount);
        System.out.println("권한 삭제 건수: " + deleteCount);
        System.out.println("총 처리 건수: " + (saveCount + deleteCount));

        return dtoList.size();
    }
} 