package com.exchange.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record MultiConversionRequest(
        
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        
        @NotEmpty(message = "At least one target currency is required")
        List<@NotNull @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters") String> currencies
) {
    
    public MultiConversionRequest {
        if (currencies != null) {
            currencies = currencies.stream()
                    .map(String::toUpperCase)
                    .toList();
        }
    }
}
