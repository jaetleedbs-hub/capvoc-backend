package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptUserMenuAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserMenuAuthrtPkey;

@Repository
public interface CmptUserMenuAuthrtRepo extends JpaRepository<CmptUserMenuAuthrtEntity, CmptUserMenuAuthrtPkey> {
} 