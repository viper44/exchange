package com.exchange.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ExternalApiException {

  public UnauthorizedException(String apiName, String message) {
    super(apiName, HttpStatus.UNAUTHORIZED.value(), message);
  }
}
