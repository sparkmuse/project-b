package com.searchmetrics.coding.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.searchmetrics.coding.config.CoinBaseProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("CoinBaseClientTest returns optional")
class CoinBaseClientTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    private CoinBaseProperties properties;

    private CoinBaseClient coinBaseClient;

    @BeforeAll
    static void setWireMock() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {
        when(properties.getUrl()).thenReturn("http://localhost:8090/url");
        this.coinBaseClient = new CoinBaseClient(restTemplate, properties);
    }

    @Test
    @DisplayName("of price")
    void returnsPrice() {

        CoinBasePrice expected = new CoinBasePrice();
        Data data = Data.builder()
                .amount(7000.234d)
                .base("BTC")
                .currency("USD")
                .build();
        expected.setData(data);

        String body = "{\"data\":{\"base\":\"BTC\",\"currency\":\"USD\",\"amount\":\"7000.234\"}}";
        wireMockServer.stubFor(get(urlEqualTo("/url"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withBody(body)));

        Optional<CoinBasePrice> actual = coinBaseClient.getPrice();

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    @DisplayName("of empty when no body returned")
    void returnsEmptyOptional() {

        CoinBasePrice expected = new CoinBasePrice();
        Data data = Data.builder()
                .amount(7000.234d)
                .base("BTC")
                .currency("USD")
                .build();
        expected.setData(data);

        wireMockServer.stubFor(get(urlEqualTo("/url"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withBody("")));

        Optional<CoinBasePrice> actual = coinBaseClient.getPrice();

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("of empty when invalid json")
    void returnsEmptyOptionalInvalid() {

        CoinBasePrice expected = new CoinBasePrice();
        Data data = Data.builder()
                .amount(7000.234d)
                .base("BTC")
                .currency("USD")
                .build();
        expected.setData(data);

        wireMockServer.stubFor(get(urlEqualTo("/url"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withBody("invalid-json")));

        Optional<CoinBasePrice> actual = coinBaseClient.getPrice();

        assertThat(actual).isEmpty();
    }
}