package com.exchange.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

  public static final String EXCHANGE_RATE_CACHE = "exchangeRates";
  public static final String ALL_RATES_CACHE = "allRates";

  @Bean
  public CacheManager cacheManager() {
    log.debug("Configuring Caffeine cache manager");

    var cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(caffeineCacheBuilder());
    cacheManager.setCacheNames(List.of(EXCHANGE_RATE_CACHE, ALL_RATES_CACHE));
    return cacheManager;
  }

  private Caffeine<Object, Object> caffeineCacheBuilder() {
    return Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(60))
        .maximumSize(1000)
        .recordStats();
  }
}
