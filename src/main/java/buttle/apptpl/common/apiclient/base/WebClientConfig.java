package buttle.apptpl.common.apiclient.base;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebClient 관련 Bean 설정
 */
@Configuration
@EnableConfigurationProperties(WebClientConfigProperties.class)
public class WebClientConfig {

    /**
     * WebClientAdaptor Bean 등록
     */
    @Bean(name = "webClientAdaptor")
    public WebClientAdaptor webClientAdaptor(WebClientConfigProperties webClientConfigProperties) {
        return new WebClientAdaptor(webClientConfigProperties);
    }
}
