package com.searchmetrics.coding.client;

import com.searchmetrics.coding.config.CoinBaseProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinBaseClient {

    private final RestTemplate restTemplate;
    private final CoinBaseProperties properties;

    public Optional<CoinBasePrice> getPrice() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CoinBasePrice> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<CoinBasePrice> exchange = restTemplate.exchange(properties.getUrl(), HttpMethod.GET, entity, CoinBasePrice.class);

            if (exchange.getBody() != null) {
                return Optional.of(exchange.getBody());
            }
        } catch (RestClientException ex) {
            log.info("error getting the price", ex);
        }

        return Optional.empty();
    }
}
