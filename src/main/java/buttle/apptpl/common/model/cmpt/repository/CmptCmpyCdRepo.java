package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptCmpyCdEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptCmpyCdPkey;

@Repository
public interface CmptCmpyCdRepo extends JpaRepository<CmptCmpyCdEntity, CmptCmpyCdPkey> {
}
