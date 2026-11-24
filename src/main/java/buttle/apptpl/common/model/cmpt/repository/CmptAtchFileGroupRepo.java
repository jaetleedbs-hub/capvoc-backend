package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptAtchFileGroupEntity;

@Repository
public interface CmptAtchFileGroupRepo extends JpaRepository<CmptAtchFileGroupEntity, String> {
}

