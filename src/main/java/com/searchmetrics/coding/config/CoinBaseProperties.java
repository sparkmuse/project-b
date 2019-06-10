package com.searchmetrics.coding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.coinbase.client")
@Getter
@Setter
public class CoinBaseProperties {
    private String url;
}
