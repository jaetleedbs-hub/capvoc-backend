package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptSrvcQstnFileEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptSrvcQstnFilePkey;

@Repository
public interface CmptSrvcQstnFileRepo extends JpaRepository<CmptSrvcQstnFileEntity, CmptSrvcQstnFilePkey> {
}
