package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptPrgrmEntity;

@Repository
public interface CmptPrgrmRepo extends JpaRepository<CmptPrgrmEntity, String> {
} 