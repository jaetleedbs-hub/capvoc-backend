package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptI18nEntity;

@Repository
public interface CmptI18nRepo extends JpaRepository<CmptI18nEntity, String> {
} 