package buttle.apptpl.common.model.sup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buttle.apptpl.common.model.sup.entity.SupMemoCtgyEntity;
import buttle.apptpl.common.model.sup.entity.SupMemoCtgyPkey;

@Repository
public interface SupMemoCtgyRepo extends JpaRepository<SupMemoCtgyEntity, SupMemoCtgyPkey> {
} 