package buttle.apptpl.common.model.logt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buttle.apptpl.common.model.logt.entity.LogtAccessLogEntity;

@Repository
public interface LogtAccessLogRepo extends JpaRepository<LogtAccessLogEntity, String> {
} 