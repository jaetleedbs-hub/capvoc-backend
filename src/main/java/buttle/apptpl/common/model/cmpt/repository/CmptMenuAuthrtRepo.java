package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptMenuAuthrtEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptMenuAuthrtPkey;

@Repository
public interface CmptMenuAuthrtRepo extends JpaRepository<CmptMenuAuthrtEntity, CmptMenuAuthrtPkey> {
} 