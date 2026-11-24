package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptCdEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptCdPkey;

import java.util.List;

@Repository
public interface CmptCdRepo extends JpaRepository<CmptCdEntity, CmptCdPkey> {
    
    /**
     * 코드그룹, 로케일, 사용여부로 코드 목록 조회
     * @param cdGroup 코드그룹
     * @param locale 로케일
     * @param useYn 사용여부
     * @return 코드 목록
     */
    List<CmptCdEntity> findByIdCdGroupAndIdLocaleAndUseYn(String cdGroup, String locale, String useYn);
    
    /**
     * 코드그룹, 로케일, 코드명, 사용여부로 코드 목록 조회
     * @param cdGroup 코드그룹
     * @param locale 로케일
     * @param cdNm 코드명
     * @param useYn 사용여부
     * @return 코드 목록
     */
    List<CmptCdEntity> findByIdCdGroupAndIdLocaleAndCdNmAndUseYn(String cdGroup, String locale, String cdNm, String useYn);
    
} 