package com.exchange.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.exchange.dto.request.MultiConversionRequest;
import com.exchange.dto.response.ConversionResponse;
import com.exchange.dto.response.ExchangeRateResponse;
import com.exchange.dto.response.MultiConversionResponse;
import com.exchange.service.ExchangeRateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ExchangeRateService exchangeRateService;

  @Test
  void getExchangeRate_ValidRequest_ReturnsOk() throws Exception {
    var from = "USD";
    var to = "EUR";
    var mockResponse = ExchangeRateResponse.builder()
        .from(from)
        .to(to)
        .rate(new BigDecimal("0.8642"))
        .timestamp(LocalDateTime.now())
        .build();

    when(exchangeRateService.getExchangeRate(from, to)).thenReturn(mockResponse);

    mockMvc.perform(get("/api/v1/exchange-rates/{from}/{to}", from, to))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.from").value(from))
        .andExpect(jsonPath("$.to").value(to))
        .andExpect(jsonPath("$.rate").value(0.8642));
  }

  @Test
  void getExchangeRate_InvalidCurrency_ReturnsBadRequest() throws Exception {
    var from = "USD";
    var to = "INVALID";

    mockMvc.perform(get("/api/v1/exchange-rates/{from}/{to}", from, to))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value(
            "getExchangeRate.to: Currency code must be 3 uppercase letters"));
  }

  @Test
  void convertCurrency_ValidRequest_ReturnsOk() throws Exception {
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

    when(exchangeRateService.convertCurrency(from, to, amount)).thenReturn(mockResponse);

    mockMvc.perform(get("/api/v1/conversions/{from}/{to}?amount={amount}", from, to, amount))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.from").value(from))
        .andExpect(jsonPath("$.to").value(to))
        .andExpect(jsonPath("$.originalAmount").value(100.00))
        .andExpect(jsonPath("$.convertedAmount").value(86.42));
  }

  @Test
  void convertCurrency_NegativeAmount_ReturnsBadRequest() throws Exception {
    var from = "USD";
    var to = "EUR";
    var negativeAmount = new BigDecimal("-100.00");

    mockMvc.perform(
            get("/api/v1/conversions/{from}/{to}?amount={amount}", from, to, negativeAmount))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("convertCurrency.amount: Amount must be positive"));
  }

  @Test
  void convertToMultipleCurrencies_ValidRequest_ReturnsOk() throws Exception {
    var from = "USD";
    var request = new MultiConversionRequest(
        new BigDecimal("100.00"),
        List.of("EUR", "GBP")
    );

    var mockResponse = MultiConversionResponse.builder()
        .from(from)
        .originalAmount(request.amount())
        .conversions(List.of(
            MultiConversionResponse.ConversionDetail.builder()
                .to("EUR")
                .convertedAmount(new BigDecimal("86.42"))
                .exchangeRate(new BigDecimal("0.8642"))
                .build(),
            MultiConversionResponse.ConversionDetail.builder()
                .to("GBP")
                .convertedAmount(new BigDecimal("76.34"))
                .exchangeRate(new BigDecimal("0.7634"))
                .build()
        ))
        .timestamp(LocalDateTime.now())
        .build();

    when(exchangeRateService.convertToMultipleCurrencies(eq(from),
        any(MultiConversionRequest.class)))
        .thenReturn(mockResponse);

    mockMvc.perform(post("/api/v1/conversions/{from}", from)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.from").value(from))
        .andExpect(jsonPath("$.originalAmount").value(100.00))
        .andExpect(jsonPath("$.conversions").isArray())
        .andExpect(jsonPath("$.conversions[0].to").value("EUR"))
        .andExpect(jsonPath("$.conversions[1].to").value("GBP"));
  }

}
