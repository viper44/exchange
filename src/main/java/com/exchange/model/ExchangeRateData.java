package com.exchange.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ExchangeRateData {
    private String baseCurrency;
    private Map<String, BigDecimal> rates;
    private LocalDateTime timestamp;
}
