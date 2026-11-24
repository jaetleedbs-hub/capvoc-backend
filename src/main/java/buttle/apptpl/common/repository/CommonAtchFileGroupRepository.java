package buttle.apptpl.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.common.entity.CommonAtchFileGroupEntity;

/**
 * 공통 첨부파일 그룹 Repository
 */
@Repository
public interface CommonAtchFileGroupRepository extends JpaRepository<CommonAtchFileGroupEntity, String> {
}
