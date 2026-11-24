package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buttle.apptpl.common.model.cmpt.entity.CmptAuthrtHstryEntity;

/**
 * 권한이력 Repository
 */
@Repository
public interface CmptAuthrtHstryRepo extends JpaRepository<CmptAuthrtHstryEntity, String> {
} 