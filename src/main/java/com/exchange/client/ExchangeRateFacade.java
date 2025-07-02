package com.exchange.client;

import com.exchange.client.exchangeratehost.ExchangeRateHostClient;
import com.exchange.config.CacheConfig;
import com.exchange.dto.response.ConversionResponse;
import com.exchange.model.ExchangeRateData;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Primary
public class ExchangeRateFacade implements ExchangeRateClient {

  private final ExchangeRateHostClient hostClient;

  @Override
  @Cacheable(value = CacheConfig.EXCHANGE_RATE_CACHE,
      key = "@cacheKeyGenerator.generateExchangeRateKey(#fromCurrency, #toCurrency)")
  public ExchangeRateData getExchangeRate(String fromCurrency, String toCurrency) {
    return hostClient.getExchangeRate(fromCurrency, toCurrency);
  }

  @Override
  @Cacheable(value = CacheConfig.ALL_RATES_CACHE,
      key = "@cacheKeyGenerator.generateAllRatesKey(#baseCurrency, #targetCurrencies)")
  public ExchangeRateData getAllExchangeRates(String baseCurrency, List<String> targetCurrencies) {
    return hostClient.getAllExchangeRates(baseCurrency, targetCurrencies);
  }

  @Override
  public ConversionResponse convertSingle(String fromCurrency, String toCurrency,
      BigDecimal amount) {
    return hostClient.convertSingle(fromCurrency, toCurrency, amount);
  }
}
