package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptUmsRcvrEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUmsRcvrPkey;

@Repository
public interface CmptUmsRcvrRepo extends JpaRepository<CmptUmsRcvrEntity, CmptUmsRcvrPkey> {
}
