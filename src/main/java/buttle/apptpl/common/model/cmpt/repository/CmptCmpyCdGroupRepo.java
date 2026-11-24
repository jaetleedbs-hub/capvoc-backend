package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptCmpyCdGroupEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptCmpyCdGroupPkey;

@Repository
public interface CmptCmpyCdGroupRepo extends JpaRepository<CmptCmpyCdGroupEntity, CmptCmpyCdGroupPkey> {
}
