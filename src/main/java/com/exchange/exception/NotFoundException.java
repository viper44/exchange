package com.exchange.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ExternalApiException {

  public NotFoundException(String provider, String message) {
    super(provider, HttpStatus.NOT_FOUND.value(), message);
  }
}
