package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptPrgrmAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptPrgrmAuthrtPkey;

@Repository
public interface CmptPrgrmAuthrtRepo extends JpaRepository<CmptPrgrmAuthrtEntity, CmptPrgrmAuthrtPkey> {
} 