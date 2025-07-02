package com.exchange.client;

import com.exchange.dto.response.ConversionResponse;
import com.exchange.model.ExchangeRateData;
import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRateClient {

  ExchangeRateData getExchangeRate(String fromCurrency, String toCurrency);

  ExchangeRateData getAllExchangeRates(String baseCurrency, List<String> targetCurrencies);

  ConversionResponse convertSingle(String fromCurrency, String toCurrency, BigDecimal amount);
}
