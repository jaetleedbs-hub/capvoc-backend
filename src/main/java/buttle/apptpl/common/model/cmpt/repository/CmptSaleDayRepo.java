package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptSaleDayEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptSaleDayPkey;

@Repository
public interface CmptSaleDayRepo extends JpaRepository<CmptSaleDayEntity, CmptSaleDayPkey> {
} 