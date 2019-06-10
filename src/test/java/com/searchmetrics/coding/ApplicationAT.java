package com.searchmetrics.coding;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.searchmetrics.coding.repository.PriceRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "app.schedule.fixed-rate-milli=10",
        "app.coinbase.client.url=http://localhost:8090/url"
})
@AutoConfigureMockMvc
class ApplicationAT {

    private static WireMockServer wireMockServer;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setWireMock() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }


    @Test
    @DisplayName("returns latest price")
    void getLatestPrice() throws Exception {

        String body = "{\"data\":{\"base\":\"BTC\",\"currency\":\"USD\",\"amount\":\"7000.234\"}}";
        wireMockServer.stubFor(get(urlEqualTo("/url"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withBody(body)));

        await()
                .atMost(1000, TimeUnit.MILLISECONDS)
                .until(() -> !priceRepository.findAll().isEmpty());

        mockMvc.perform(MockMvcRequestBuilders.get("/prices")
                .param("latest", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.symbol").value("USD"))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.rate").value(7000.234d));
    }
}