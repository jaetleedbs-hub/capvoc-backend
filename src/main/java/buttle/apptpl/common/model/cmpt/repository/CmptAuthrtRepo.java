package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buttle.apptpl.common.model.cmpt.entity.CmptAuthrtEntity;

/**
 * 권한 Repository
 */
@Repository
public interface CmptAuthrtRepo extends JpaRepository<CmptAuthrtEntity, String> {
}