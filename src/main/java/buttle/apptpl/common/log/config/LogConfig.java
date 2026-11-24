package buttle.apptpl.common.log.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "svc-env.log")
public class LogConfig {

    private boolean usePersonalLog;
    private boolean useApiLog;
   
}
