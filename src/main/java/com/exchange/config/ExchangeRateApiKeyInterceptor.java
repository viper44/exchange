package com.exchange.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExchangeRateApiKeyInterceptor implements RequestInterceptor {

  private final String apiKey;
  
  public ExchangeRateApiKeyInterceptor(String apiKey) {
    this.apiKey = apiKey;
  }

  @Override
  public void apply(RequestTemplate template) {
    template.query("access_key", apiKey);
    log.debug("Added API key to request: {}", template.url());
  }
}
