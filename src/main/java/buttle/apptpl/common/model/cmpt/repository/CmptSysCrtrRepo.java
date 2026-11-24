package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buttle.apptpl.common.model.cmpt.entity.CmptSysCrtrEntity;

/**
 * 시스템기준정보관리 Repository
 */
@Repository
public interface CmptSysCrtrRepo extends JpaRepository<CmptSysCrtrEntity, String> {
}
