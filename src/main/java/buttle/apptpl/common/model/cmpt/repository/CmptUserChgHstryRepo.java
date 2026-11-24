package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptUserChgHstryEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserChgHstryPkey;

@Repository
public interface CmptUserChgHstryRepo extends JpaRepository<CmptUserChgHstryEntity, CmptUserChgHstryPkey> {
} 