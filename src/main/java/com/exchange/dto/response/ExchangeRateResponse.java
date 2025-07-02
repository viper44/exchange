package com.exchange.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ExchangeRateResponse {
    private String from;
    private String to;
    private BigDecimal rate;
    private LocalDateTime timestamp;
}
