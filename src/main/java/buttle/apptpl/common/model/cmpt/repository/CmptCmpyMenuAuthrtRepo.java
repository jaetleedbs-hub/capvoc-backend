package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptCmpyMenuAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptCmpyMenuAuthrtPkey;

@Repository
public interface CmptCmpyMenuAuthrtRepo extends JpaRepository<CmptCmpyMenuAuthrtEntity, CmptCmpyMenuAuthrtPkey> {
}
