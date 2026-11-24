package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptMenuAuthrtHstryEntity;

@Repository
public interface CmptMenuAuthrtHstryRepo extends JpaRepository<CmptMenuAuthrtHstryEntity, Integer> {
} 