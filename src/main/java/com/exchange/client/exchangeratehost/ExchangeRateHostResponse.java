package com.exchange.client.exchangeratehost;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

public record ExchangeRateHostResponse(
    @JsonProperty("success")
    boolean success,
    
    @JsonProperty("source")
    String base,
    
    @JsonProperty("timestamp")
    Long timestamp,
    
    @JsonProperty("terms")
    String terms,
    
    @JsonProperty("privacy")
    String privacy,
    
    @JsonProperty("quotes")
    Map<String, BigDecimal> rates,
    
    @JsonProperty("error")
    ErrorInfo error
) {
    
    public record ErrorInfo(
        @JsonProperty("code")
        String code,
        
        @JsonProperty("info")
        String info
    ) {}
    
    public boolean isSuccessful() {
        return success && error == null;
    }
}
