package buttle.apptpl.common.config.caffeine;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CaffeineConfig {

    @Value("${svc-env.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
                .map(cacheType -> {
                    final Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder().recordStats()
                            .maximumSize(cacheType.getMaximumSize());

                    if (cacheType == CacheType.JWT_REFRESH_TOKEN) {
                        caffeineBuilder.expireAfterWrite(refreshTokenValidityInSeconds, TimeUnit.SECONDS);
                    } else {
                        caffeineBuilder.expireAfterWrite(cacheType.getExpiredAfterWrite(), TimeUnit.SECONDS);
                    }

                    return new CaffeineCache(cacheType.getCacheNameWithCompany(), caffeineBuilder.build());
                })
                .collect(Collectors.toList());
        cacheManager.setCaches(caches);
        return cacheManager;
    }
} 