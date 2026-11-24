package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptCmpyInfEntity;

@Repository
public interface CmptCmpyInfRepo extends JpaRepository<CmptCmpyInfEntity, String> {
}
