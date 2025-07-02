package com.exchange.client.exchangeratehost;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record ConvertResponse(
    @JsonProperty("success") boolean success,
    @JsonProperty("query") ConvertQuery query,
    @JsonProperty("info") ConvertInfo info,
    @JsonProperty("result") BigDecimal result,
    @JsonProperty("error") ErrorInfo error
) {

  public record ConvertQuery(
      @JsonProperty("from") String from,
      @JsonProperty("to") String to,
      @JsonProperty("amount") BigDecimal amount
  ) {

  }

  public record ConvertInfo(
      @JsonProperty("timestamp") long timestamp,
      @JsonProperty("quote") BigDecimal quote
  ) {

  }

  public record ErrorInfo(
      @JsonProperty("code") String code,
      @JsonProperty("info") String info
  ) {

  }

  public boolean isSuccessful() {
    return success && error == null;
  }

  public BigDecimal getRate() {
    return info != null ? info.quote() : null;
  }
}
