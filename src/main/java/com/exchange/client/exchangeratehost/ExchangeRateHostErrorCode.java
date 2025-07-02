package com.exchange.client.exchangeratehost;

import java.util.Arrays;
import java.util.Optional;

enum ExchangeRateHostErrorCode {
  MISSING_API_KEY("101"),
  INVALID_FUNCTION("103"),
  RATE_LIMIT_EXCEEDED("104"),
  INVALID_TO("402"),
  INVALID_AMOUNT("403"),
  RESOURCE_NOT_FOUND("404"),
  INVALID_SOURCE("201"),
  INVALID_CURRENCIES("202"),
  NO_RESULTS("106");

  private final String code;

  ExchangeRateHostErrorCode(String code) {
    this.code = code;
  }

  public static Optional<ExchangeRateHostErrorCode> from(String code) {
    return Arrays.stream(values())
        .filter(e -> e.code.equals(code))
        .findFirst();
  }
}
