package com.exchange.controller;

import com.exchange.dto.request.MultiConversionRequest;
import com.exchange.dto.response.AllExchangeRatesResponse;
import com.exchange.dto.response.ConversionResponse;
import com.exchange.dto.response.ExchangeRateResponse;
import com.exchange.dto.response.MultiConversionResponse;
import com.exchange.service.ExchangeRateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ExchangeRateController {

  private final ExchangeRateService exchangeRateService;

  @GetMapping("/exchange-rates/{from}/{to}")
  public ResponseEntity<ExchangeRateResponse> getExchangeRate(
      @PathVariable @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters") String from,
      @PathVariable @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters") String to) {

    log.debug("GET /exchange-rates/{}/{}", from, to);

    var response = exchangeRateService.getExchangeRate(from, to);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/exchange-rates/{from}")
  public ResponseEntity<AllExchangeRatesResponse> getAllExchangeRates(
      @PathVariable @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters") String from) {
    var response = exchangeRateService.getAllExchangeRates(from);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/conversions/{from}/{to}")
  public ResponseEntity<ConversionResponse> convertCurrency(
      @PathVariable @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters") String from,
      @PathVariable @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters") String to,
      @RequestParam @Positive(message = "Amount must be positive") BigDecimal amount) {

    var response = exchangeRateService.convertCurrency(from, to, amount);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/conversions/{from}")
  public ResponseEntity<MultiConversionResponse> convertToMultipleCurrencies(
      @PathVariable @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters") String from,
      @Valid @RequestBody MultiConversionRequest request) {

    var response = exchangeRateService.convertToMultipleCurrencies(from, request);
    return ResponseEntity.ok(response);
  }

}
