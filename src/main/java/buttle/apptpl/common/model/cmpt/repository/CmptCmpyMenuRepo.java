package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptCmpyMenuEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptCmpyMenuPkey;

@Repository
public interface CmptCmpyMenuRepo extends JpaRepository<CmptCmpyMenuEntity, CmptCmpyMenuPkey> {
}
