package com.exchange.client.exchangeratehost;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(
    name = "${client.exchange-rate-host.name}",
    url = "${client.exchange-rate-host.url}",
    configuration = com.exchange.config.ExchangeRateHostFeignClientConfig.class
)
public interface ExchangeRateHostFeignClient {

    @GetMapping("/live")
    ExchangeRateHostResponse getLatestRates(
            @RequestParam("source") String base,
            @RequestParam(value = "currencies", required = false) String currencies
    );

    @GetMapping("/convert")
    ConvertResponse convertCurrency(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("amount") BigDecimal amount
    );
}
