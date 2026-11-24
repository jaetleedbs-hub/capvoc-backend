package buttle.apptpl.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.common.entity.CommonAtchFileEntity;

import java.util.List;
import java.util.Optional;

/**
 * 공통 첨부파일 Repository
 */
@Repository
public interface CommonAtchFileRepository extends JpaRepository<CommonAtchFileEntity, String> {
    
    /**
     * 그룹 ID와 삭제 여부로 파일 목록 조회
     */
    List<CommonAtchFileEntity> findByAtchFileGroupIdAndDelYn(String atchFileGroupId, String delYn);
    
    /**
     * 파일 ID와 삭제 여부로 파일 조회
     */
    Optional<CommonAtchFileEntity> findByIdAndDelYn(String id, String delYn);
}
