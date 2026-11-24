package buttle.apptpl.common.log.personal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogtPersonalAccessLogRepo extends JpaRepository<LogtPersonalAccessLogEntity, LogtPersonalAccessLogPkey> {
}
