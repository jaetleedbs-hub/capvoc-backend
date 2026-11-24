package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buttle.apptpl.common.model.cmpt.entity.CmptOgnzEntity;

/**
 * 조직 Repository
 */
@Repository
public interface CmptOgnzRepo extends JpaRepository<CmptOgnzEntity, String> {
}