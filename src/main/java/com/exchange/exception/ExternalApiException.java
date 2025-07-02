package com.exchange.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExternalApiException extends RuntimeException {
    
    private final String apiName;
    private final int statusCode;
    
    public ExternalApiException(String apiName, String message) {
        super(message);
        this.apiName = apiName;
        this.statusCode = HttpStatus.SERVICE_UNAVAILABLE.value();
    }


    public ExternalApiException(String apiName, int statusCode, String message) {
        super(message);
        this.apiName = apiName;
        this.statusCode = statusCode;
    }
    
    public ExternalApiException(String apiName, int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.apiName = apiName;
        this.statusCode = statusCode;
    }
}
