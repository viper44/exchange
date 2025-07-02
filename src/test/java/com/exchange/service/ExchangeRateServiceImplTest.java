package com.exchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.exchange.client.ExchangeRateClient;
import com.exchange.dto.request.MultiConversionRequest;
import com.exchange.dto.response.ConversionResponse;
import com.exchange.dto.response.ExchangeRateResponse;
import com.exchange.dto.response.MultiConversionResponse;
import com.exchange.exception.CurrencyConversionException;
import com.exchange.model.ExchangeRateData;
import com.exchange.service.impl.ExchangeRateServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceImplTest {

  @Mock
  private ExchangeRateClient exchangeRateClient;

  @InjectMocks
  private ExchangeRateServiceImpl exchangeRateService;

  @Test
  void getExchangeRate_ValidCurrencies_ReturnsCorrectResponse() {
    var from = "USD";
    var to = "EUR";
    var expectedRate = new BigDecimal("0.8642");

    var mockData = new ExchangeRateData();
    mockData.setBaseCurrency(from);
    mockData.setRates(Map.of(to, expectedRate));
    mockData.setTimestamp(LocalDateTime.now());

    when(exchangeRateClient.getExchangeRate(from, to)).thenReturn(mockData);

    var response = exchangeRateService.getExchangeRate(from, to);

    assertThat(response).isNotNull();
    assertThat(response.getFrom()).isEqualTo(from);
    assertThat(response.getTo()).isEqualTo(to);
    assertThat(response.getRate()).isEqualByComparingTo(expectedRate.setScale(4));

    verify(exchangeRateClient).getExchangeRate(from, to);
  }

  @Test
  void getExchangeRate_SameCurrency_ReturnsRateOfOne() {
    var currency = "USD";

    var response = exchangeRateService.getExchangeRate(currency, currency);

    assertThat(response).isNotNull();
    assertThat(response.getFrom()).isEqualTo(currency);
    assertThat(response.getTo()).isEqualTo(currency);
    assertThat(response.getRate()).isEqualByComparingTo(BigDecimal.ONE);

    verifyNoInteractions(exchangeRateClient);
  }

  @Test
  void getExchangeRate_RateNotFound_ThrowsCurrencyConversionException() {
    var from = "USD";
    var to = "EUR";

    var mockData = new ExchangeRateData();
    mockData.setBaseCurrency(from);
    mockData.setRates(Map.of());
    mockData.setTimestamp(LocalDateTime.now());

    when(exchangeRateClient.getExchangeRate(from, to)).thenReturn(mockData);

    assertThatThrownBy(() -> exchangeRateService.getExchangeRate(from, to))
        .isInstanceOf(CurrencyConversionException.class)
        .hasMessageContaining("Exchange rate not found for currency pair");
  }

  @Test
  void convertCurrency_ValidInputs_ReturnsCorrectConversion() {
    var from = "USD";
    var to = "EUR";
    var amount = new BigDecimal("100.00");

    var mockResponse = ConversionResponse.builder()
        .from(from)
        .to(to)
        .originalAmount(amount)
        .convertedAmount(new BigDecimal("86.42"))
        .exchangeRate(new BigDecimal("0.8642"))
        .timestamp(LocalDateTime.now())
        .build();

    when(exchangeRateClient.convertSingle(from, to, amount)).thenReturn(mockResponse);

    var response = exchangeRateService.convertCurrency(from, to, amount);

    assertThat(response).isNotNull();
    assertThat(response.getFrom()).isEqualTo(from);
    assertThat(response.getTo()).isEqualTo(to);
    assertThat(response.getOriginalAmount()).isEqualByComparingTo(amount);
    assertThat(response.getConvertedAmount()).isEqualByComparingTo(new BigDecimal("86.42"));

    verify(exchangeRateClient).convertSingle(from, to, amount);
  }

  @Test
  void convertToMultipleCurrencies_ValidInputs_ReturnsCorrectConversions() {
    var from = "USD";
    var amount = new BigDecimal("100.00");
    var targetCurrencies = List.of("EUR", "GBP");

    var request = new MultiConversionRequest(amount, targetCurrencies);

    var rates = Map.of(
        "EUR", new BigDecimal("0.8642"),
        "GBP", new BigDecimal("0.7634")
    );

    var mockData = new ExchangeRateData();
    mockData.setBaseCurrency(from);
    mockData.setRates(rates);
    mockData.setTimestamp(LocalDateTime.now());

    when(exchangeRateClient.getAllExchangeRates(from, targetCurrencies)).thenReturn(mockData);

    var response = exchangeRateService.convertToMultipleCurrencies(from, request);

    assertThat(response).isNotNull();
    assertThat(response.getFrom()).isEqualTo(from);
    assertThat(response.getOriginalAmount()).isEqualByComparingTo(amount);
    assertThat(response.getConversions()).hasSize(2);
  }

}
