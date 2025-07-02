package com.exchange.exception;

import lombok.Getter;

@Getter
public class CurrencyConversionException extends RuntimeException {
    
    private final String fromCurrency;
    private final String toCurrency;
    
    public CurrencyConversionException(String fromCurrency, String toCurrency, String message) {
        super(message);
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }
}
