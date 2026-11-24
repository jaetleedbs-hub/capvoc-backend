package buttle.apptpl.common.apiclient.base;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "interface.webclient", ignoreUnknownFields = true)
public class WebClientConfigProperties {

    /**
     * 전역 SSL 인증서 신뢰 설정.
     *
     * <p>true로 설정하면 모든 HTTPS 서버의 인증서 검증을 비활성화한다(중간자 공격에 노출).
     * 과거 기본값이 true여서 <b>설정을 하지 않으면 전 구간이 무검증</b>이었다.
     * 안전한 기본값(false)으로 두고, 사설 인증서를 쓰는 비운영 환경에서만 명시적으로 켠다.
     */
    private boolean globalSslTrustAll = false;

    /**
     * WebClient 서버 설정 맵
     * 각 서버별로 baseUrl, timeoutMillis 등을 설정할 수 있습니다.
     */
    private Map<String, WebClientServerConfig> servers;
    
    /**
     * 타입 안전성을 위한 getter 메서드
     */
    public Map<String, WebClientServerConfig> getServers() {
        if (servers == null) {
            return new HashMap<>();
        }
        return new HashMap<>(servers);
    }

    
}

