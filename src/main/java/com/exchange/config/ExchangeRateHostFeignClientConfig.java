package com.exchange.config;

import feign.Logger;
import feign.Logger.Level;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ExchangeRateHostFeignClientConfig {

  @Value("${client.exchange-rate-host.api-key}")
  private String apiKey;

  @Bean
  public RequestInterceptor requestInterceptor() {
    return new ExchangeRateApiKeyInterceptor(apiKey);
  }

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Level.BASIC;
  }
}
