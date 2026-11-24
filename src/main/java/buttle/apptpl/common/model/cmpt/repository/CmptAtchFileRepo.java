package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptAtchFileEntity;

@Repository
public interface CmptAtchFileRepo extends JpaRepository<CmptAtchFileEntity, String> {
}

