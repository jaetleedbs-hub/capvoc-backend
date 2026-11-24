package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdHstryEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdHstryPkey;

@Repository
public interface CmptPswdHstryRepo extends JpaRepository<CmptPswdHstryEntity, CmptPswdHstryPkey> {
} 