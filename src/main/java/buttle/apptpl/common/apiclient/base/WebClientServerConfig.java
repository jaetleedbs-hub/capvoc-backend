package buttle.apptpl.common.apiclient.base;

import lombok.Data;

@Data
public class WebClientServerConfig {
    private String baseUrl;
    private Integer timeoutMillis;

    private boolean sslTrustAll = false;
    private String keystorePath;
    private String keystorePassword;
    private String truststorePath;
    private String truststorePassword;
    private boolean http2Enabled = false;
    private int maxConnections = 100;
    private int maxIdleTime = 30000;
    private int maxRetries = 3;
    private long retryDelayMillis = 1000;
}
