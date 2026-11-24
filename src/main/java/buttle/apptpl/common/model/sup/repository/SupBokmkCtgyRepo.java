package buttle.apptpl.common.model.sup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buttle.apptpl.common.model.sup.entity.SupBokmkCtgyEntity;
import buttle.apptpl.common.model.sup.entity.SupBokmkCtgyPkey;

@Repository
public interface SupBokmkCtgyRepo extends JpaRepository<SupBokmkCtgyEntity, SupBokmkCtgyPkey> {
} 