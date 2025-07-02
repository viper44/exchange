package com.exchange.mapper;

import static java.time.Instant.ofEpochSecond;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.exchange.client.exchangeratehost.ExchangeRateHostResponse;
import com.exchange.model.ExchangeRateData;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = SPRING)
public interface ExchangeRateMapper {

  @Mapping(target = "baseCurrency", source = "base")
  @Mapping(target = "rates", source = "response", qualifiedByName = "mapRates")
  @Mapping(target = "timestamp", source = "response", qualifiedByName = "mapTimestamp")
  ExchangeRateData toExchangeRateData(ExchangeRateHostResponse response);

  @Named("mapTimestamp")
  default LocalDateTime mapTimestamp(ExchangeRateHostResponse response) {
    if (response.timestamp() != null) {
      return ofEpochSecond(response.timestamp())
          .atZone(java.time.ZoneId.systemDefault())
          .toLocalDateTime();
    }
    return LocalDateTime.now();
  }

  @Named("mapRates")
  default Map<String, BigDecimal> mapRates(ExchangeRateHostResponse response) {
    if (response.rates() == null) {
      return new HashMap<>();
    }

    var transformedRates = new HashMap<String, BigDecimal>();
    var baseCurrency = response.base();

    for (var entry : response.rates().entrySet()) {
      var originalKey = entry.getKey();
      String targetCurrency;

      if (baseCurrency != null && originalKey.startsWith(baseCurrency)) {
        targetCurrency = originalKey.substring(baseCurrency.length());
      } else {
        targetCurrency = originalKey;
      }

      transformedRates.put(targetCurrency, entry.getValue());
    }

    return transformedRates;
  }
}
