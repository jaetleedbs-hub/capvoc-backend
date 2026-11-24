package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptSrvcQstnEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptSrvcQstnPkey;

@Repository
public interface CmptSrvcQstnRepo extends JpaRepository<CmptSrvcQstnEntity, CmptSrvcQstnPkey> {
}
