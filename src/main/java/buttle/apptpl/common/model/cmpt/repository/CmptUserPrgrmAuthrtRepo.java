package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptUserPrgrmAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserPrgrmAuthrtPkey;

@Repository
public interface CmptUserPrgrmAuthrtRepo extends JpaRepository<CmptUserPrgrmAuthrtEntity, CmptUserPrgrmAuthrtPkey> {
} 