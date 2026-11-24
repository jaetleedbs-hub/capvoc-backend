package buttle.apptpl.common.model.sup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import buttle.apptpl.common.model.sup.entity.SupBokmkMenuEntity;
import buttle.apptpl.common.model.sup.entity.SupBokmkMenuPkey;

@Repository
public interface SupBokmkMenuRepo extends JpaRepository<SupBokmkMenuEntity, SupBokmkMenuPkey> {
} 