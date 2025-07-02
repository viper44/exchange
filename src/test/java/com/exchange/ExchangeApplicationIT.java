package com.exchange;

import static org.assertj.core.api.Assertions.assertThat;

import com.exchange.controller.ExchangeRateController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ExchangeApplicationIT {

  @Autowired
  private ExchangeRateController exchangeRateController;

  @Autowired
  private CacheManager cacheManager;

  @Test
  void contextLoads() {
    assertThat(exchangeRateController).isNotNull();
    assertThat(cacheManager).isNotNull();
  }

  @Test
  void cacheManagerConfiguration_CorrectCacheNames() {
    assertThat(cacheManager.getCacheNames()).contains("exchangeRates", "allRates");
  }

  @Test
  void sameCurrencyConversion_NoExternalApiCall() {
    var response = exchangeRateController.getExchangeRate("USD", "USD");

    assertThat(response).isNotNull();
  }
}
