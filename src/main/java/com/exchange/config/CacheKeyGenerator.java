package com.exchange.config;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CacheKeyGenerator {
    
    private static final String SEPARATOR = "_";
    private static final String ALL_CURRENCIES = "all";
    
    public String generateExchangeRateKey(String fromCurrency, String toCurrency) {
        return fromCurrency.toUpperCase() + SEPARATOR + toCurrency.toUpperCase();
    }
    
    public String generateAllRatesKey(String baseCurrency, List<String> targetCurrencies) {
        var base = baseCurrency.toUpperCase();
        
        if (targetCurrencies == null || targetCurrencies.isEmpty()) {
            return base + SEPARATOR + ALL_CURRENCIES;
        }
        
        // Efficiently create sorted, joined string
        var sortedCurrencies = targetCurrencies.stream()
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.joining(","));
        
        return base + SEPARATOR + sortedCurrencies;
    }
}
