package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.cmpt.entity.CmptUserLgnHstryEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserLgnHstryPkey;

@Repository
public interface CmptUserLgnHstryRepo extends JpaRepository<CmptUserLgnHstryEntity, CmptUserLgnHstryPkey> {
} 