package com.exchange.service;

import com.exchange.dto.request.MultiConversionRequest;
import com.exchange.dto.response.AllExchangeRatesResponse;
import com.exchange.dto.response.ConversionResponse;
import com.exchange.dto.response.ExchangeRateResponse;
import com.exchange.dto.response.MultiConversionResponse;
import java.math.BigDecimal;
import java.util.List;


public interface ExchangeRateService {

  ExchangeRateResponse getExchangeRate(String fromCurrency, String toCurrency);

  AllExchangeRatesResponse getAllExchangeRates(String baseCurrency);

  AllExchangeRatesResponse getAllExchangeRates(String baseCurrency, List<String> targetCurrencies);

  ConversionResponse convertCurrency(String fromCurrency, String toCurrency, BigDecimal amount);

  MultiConversionResponse convertToMultipleCurrencies(String fromCurrency,
      MultiConversionRequest request);
}
