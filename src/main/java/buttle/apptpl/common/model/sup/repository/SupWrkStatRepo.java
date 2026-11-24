package buttle.apptpl.common.model.sup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.sup.entity.SupWrkStatEntity;
import buttle.apptpl.common.model.sup.entity.SupWrkStatPkey;

@Repository
public interface SupWrkStatRepo extends JpaRepository<SupWrkStatEntity, SupWrkStatPkey> {
} 