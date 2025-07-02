package com.exchange.service.impl;

import com.exchange.client.ExchangeRateClient;
import com.exchange.dto.request.MultiConversionRequest;
import com.exchange.dto.response.AllExchangeRatesResponse;
import com.exchange.dto.response.ConversionResponse;
import com.exchange.dto.response.ExchangeRateResponse;
import com.exchange.dto.response.MultiConversionResponse;
import com.exchange.exception.CurrencyConversionException;
import com.exchange.service.ExchangeRateService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

  private static final int DISPLAY_SCALE = 4;

  private final ExchangeRateClient exchangeRateClient;

  @Override
  public ExchangeRateResponse getExchangeRate(String fromCurrency, String toCurrency) {
    log.debug("Processing exchange rate request: {} -> {}", fromCurrency, toCurrency);

    if (fromCurrency.equalsIgnoreCase(toCurrency)) {
      return createSameCurrencyResponse(fromCurrency, toCurrency);
    }

    var data = exchangeRateClient.getExchangeRate(fromCurrency, toCurrency);
    var rate = data.getRates().get(toCurrency.toUpperCase());

    if (rate == null) {
      throw new CurrencyConversionException(fromCurrency, toCurrency,
          "Exchange rate not found for currency pair");
    }

    return ExchangeRateResponse.builder()
        .from(fromCurrency.toUpperCase())
        .to(toCurrency.toUpperCase())
        .rate(rate.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP))
        .timestamp(LocalDateTime.now())
        .build();
  }

  @Override
  public AllExchangeRatesResponse getAllExchangeRates(String baseCurrency) {
    return getAllExchangeRates(baseCurrency, List.of());
  }

  @Override
  public AllExchangeRatesResponse getAllExchangeRates(String baseCurrency,
      List<String> targetCurrencies) {
    var data = exchangeRateClient.getAllExchangeRates(baseCurrency, targetCurrencies);

    if (data.getRates() == null || data.getRates().isEmpty()) {
      throw new CurrencyConversionException(baseCurrency, "ALL",
          "No exchange rates found for base currency");
    }

    var processedRates = data.getRates().entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().setScale(DISPLAY_SCALE, RoundingMode.HALF_UP)
        ));

    return AllExchangeRatesResponse.builder()
        .base(baseCurrency.toUpperCase())
        .rates(processedRates)
        .timestamp(LocalDateTime.now())
        .build();
  }

  @Override
  public ConversionResponse convertCurrency(String fromCurrency, String toCurrency,
      BigDecimal amount) {
    log.debug("Processing single conversion: {} {} -> {}", amount, fromCurrency, toCurrency);

    if (fromCurrency.equalsIgnoreCase(toCurrency)) {
      return createSameCurrencyConversion(fromCurrency, toCurrency, amount);
    }

    return exchangeRateClient.convertSingle(fromCurrency, toCurrency, amount);
  }

  @Override
  public MultiConversionResponse convertToMultipleCurrencies(String fromCurrency,
      MultiConversionRequest request) {
    log.debug("Processing multi-currency conversion: {} -> {}", fromCurrency, request.currencies());

    var allRates = getAllExchangeRates(fromCurrency, request.currencies());
    var conversions = request.currencies().stream()
        .map(toCurrency -> convertSingleCurrency(fromCurrency, toCurrency, request.amount(),
            allRates))
        .toList();

    return MultiConversionResponse.builder()
        .from(fromCurrency.toUpperCase())
        .originalAmount(request.amount())
        .conversions(conversions)
        .timestamp(LocalDateTime.now())
        .build();
  }

  private MultiConversionResponse.ConversionDetail convertSingleCurrency(
      String fromCurrency, String toCurrency, BigDecimal amount,
      AllExchangeRatesResponse allRates) {

    if (fromCurrency.equalsIgnoreCase(toCurrency)) {
      return createSameCurrencyConversionDetail(toCurrency, amount);
    }

    var rate = allRates.getRates().get(toCurrency.toUpperCase());
    if (rate == null) {
      throw new CurrencyConversionException(fromCurrency, toCurrency,
          "Exchange rate not available");
    }

    var convertedAmount = amount.multiply(rate).setScale(DISPLAY_SCALE, RoundingMode.HALF_UP);

    return MultiConversionResponse.ConversionDetail.builder()
        .to(toCurrency.toUpperCase())
        .convertedAmount(convertedAmount)
        .exchangeRate(rate)
        .build();
  }

  private MultiConversionResponse.ConversionDetail createSameCurrencyConversionDetail(
      String currency, BigDecimal amount) {
    return MultiConversionResponse.ConversionDetail.builder()
        .to(currency.toUpperCase())
        .convertedAmount(amount)
        .exchangeRate(BigDecimal.ONE)
        .build();
  }

  private ExchangeRateResponse createSameCurrencyResponse(String fromCurrency, String toCurrency) {
    return ExchangeRateResponse.builder()
        .from(fromCurrency.toUpperCase())
        .to(toCurrency.toUpperCase())
        .rate(BigDecimal.ONE)
        .timestamp(LocalDateTime.now())
        .build();
  }

  private ConversionResponse createSameCurrencyConversion(String fromCurrency, String toCurrency,
      BigDecimal amount) {
    return ConversionResponse.builder()
        .from(fromCurrency.toUpperCase())
        .to(toCurrency.toUpperCase())
        .originalAmount(amount)
        .convertedAmount(amount)
        .exchangeRate(BigDecimal.ONE)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
