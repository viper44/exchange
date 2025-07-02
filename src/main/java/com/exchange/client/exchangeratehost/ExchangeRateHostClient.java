package com.exchange.client.exchangeratehost;

import com.exchange.annotation.ExternalApiRetry;
import com.exchange.client.ExchangeRateClient;
import com.exchange.dto.response.ConversionResponse;
import com.exchange.exception.BadRequestException;
import com.exchange.exception.ExternalApiException;
import com.exchange.exception.NotFoundException;
import com.exchange.exception.UnauthorizedException;
import com.exchange.mapper.ExchangeRateMapper;
import com.exchange.model.ExchangeRateData;
import feign.FeignException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateHostClient implements ExchangeRateClient {

  private static final String PROVIDER_NAME = "exchangerate.host";

  private final ExchangeRateHostFeignClient feignClient;
  private final ExchangeRateMapper mapper;

  @Override
  @ExternalApiRetry
  public ExchangeRateData getExchangeRate(String fromCurrency, String toCurrency) {
    log.debug("Fetching exchange rate from API: {} -> {}", fromCurrency, toCurrency);

    try {
      var response = feignClient.getLatestRates(fromCurrency.toUpperCase(),
          toCurrency.toUpperCase());
      validate(response);
      return mapper.toExchangeRateData(response);
    } catch (FeignException ex) {
      throw new ExternalApiException(PROVIDER_NAME, ex.status(),
          "Failed to fetch exchange rate: " + ex.getMessage(), ex);
    }
  }

  @Override
  @ExternalApiRetry
  public ExchangeRateData getAllExchangeRates(String baseCurrency, List<String> targetCurrencies) {
    try {
      var response = feignClient.getLatestRates(baseCurrency.toUpperCase(), null);
      validate(response);
      return mapper.toExchangeRateData(response);
    } catch (FeignException ex) {
      throw new ExternalApiException(PROVIDER_NAME, ex.status(),
          "Failed to fetch exchange rates: " + ex.getMessage(), ex);
    }
  }

  @Override
  @ExternalApiRetry
  public ConversionResponse convertSingle(String fromCurrency, String toCurrency,
      BigDecimal amount) {
    log.debug("Converting {} {} to {} using direct API", amount, fromCurrency, toCurrency);

    try {
      var response = feignClient.convertCurrency(
          fromCurrency.toUpperCase(),
          toCurrency.toUpperCase(),
          amount
      );

      return validateAndMapConversion(response, fromCurrency, toCurrency);

    } catch (FeignException ex) {
      throw new ExternalApiException(PROVIDER_NAME, ex.status(),
          "Failed to convert currency: " + ex.getMessage(), ex);
    }
  }

  private void validate(ExchangeRateHostResponse response) {
    if (response == null || !response.isSuccessful()) {
      Optional.ofNullable(response)
          .map(ExchangeRateHostResponse::error)
          .ifPresent(this::handleErrorCode);
      throw new ExternalApiException(PROVIDER_NAME, "Unknown error from exchange rate API");
    }
  }

  private ConversionResponse validateAndMapConversion(ConvertResponse response, String fromCurrency,
      String toCurrency) {
    if (response == null || !response.isSuccessful()) {
      var errorMessage = response != null && response.error() != null
          ? response.error().info()
          : "Unknown error from conversion API";
      throw new ExternalApiException(PROVIDER_NAME, errorMessage);
    }

    return ConversionResponse.builder()
        .from(fromCurrency.toUpperCase())
        .to(toCurrency.toUpperCase())
        .originalAmount(response.query().amount())
        .convertedAmount(response.result())
        .exchangeRate(response.getRate())
        .timestamp(java.time.LocalDateTime.now())
        .build();
  }

  private void handleErrorCode(ExchangeRateHostResponse.ErrorInfo error) {
    ExchangeRateHostErrorCode.from(error.code())
        .ifPresentOrElse(code -> {
          switch (code) {
            case MISSING_API_KEY, RATE_LIMIT_EXCEEDED ->
                throw new UnauthorizedException(PROVIDER_NAME, error.info());
            case INVALID_TO, INVALID_AMOUNT, INVALID_SOURCE, INVALID_CURRENCIES, NO_RESULTS ->
                throw new BadRequestException(PROVIDER_NAME, error.info());
            case RESOURCE_NOT_FOUND, INVALID_FUNCTION ->
                throw new NotFoundException(PROVIDER_NAME, error.info());
            default -> throw new ExternalApiException(PROVIDER_NAME, error.info());
          }
        }, () -> {
          throw new ExternalApiException(PROVIDER_NAME, "Unknown API error: " + error.info());
        });
  }
}
