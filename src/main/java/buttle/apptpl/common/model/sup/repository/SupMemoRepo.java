package buttle.apptpl.common.model.sup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.sup.entity.SupMemoEntity;
import buttle.apptpl.common.model.sup.entity.SupMemoPkey;

@Repository
public interface SupMemoRepo extends JpaRepository<SupMemoEntity, SupMemoPkey> {
} 