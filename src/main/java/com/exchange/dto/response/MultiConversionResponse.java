package com.exchange.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MultiConversionResponse {
    private String from;
    private BigDecimal originalAmount;
    private List<ConversionDetail> conversions;
    private LocalDateTime timestamp;
    
    @Data
    @Builder
    public static class ConversionDetail {
        private String to;
        private BigDecimal convertedAmount;
        private BigDecimal exchangeRate;
    }
}
