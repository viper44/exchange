package com.exchange.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ExternalApiException {

  public BadRequestException(String provider, String message) {
    super(provider, HttpStatus.BAD_REQUEST.value(), message);
  }
}
