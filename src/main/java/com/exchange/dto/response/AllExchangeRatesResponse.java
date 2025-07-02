package com.exchange.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class AllExchangeRatesResponse {
    private String base;
    private Map<String, BigDecimal> rates;
    private LocalDateTime timestamp;
}
