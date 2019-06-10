package com.searchmetrics.coding.api;

import com.searchmetrics.coding.entity.Price;
import com.searchmetrics.coding.exception.InvalidDateRangeException;
import com.searchmetrics.coding.exception.PriceNotFoundException;
import com.searchmetrics.coding.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WebMvcTest(controllers = {PriceController.class, PriceControllerAdvice.class})
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceService priceService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    @DisplayName("gets the latest price")
    void getLatestPrice() throws Exception {

        LocalDateTime localDateTime = LocalDateTime.parse("2019-06-09T12:23:01");
        Price price = Price.builder()
                .id("my-id")
                .symbol("USD")
                .created(localDateTime)
                .rate(23.00d)
                .build();

        when(priceService.getLastPrice()).thenReturn(price);

        mockMvc.perform(get("/prices")
                .param("latest", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value("my-id"))
                .andExpect(jsonPath("$.symbol").value("USD"))
                .andExpect(jsonPath("$.created").value(localDateTime.toString()))
                .andExpect(jsonPath("$.rate").value(23.00d))
                .andDo(document("latest-price",
                        requestParameters(
                                parameterWithName("latest").description("get the lates price from the database")
                        )
                ));
    }

    @Test
    @DisplayName("gets the historical prices between two dates")
    void getHistoricalPrices() throws Exception {

        LocalDateTime localDateTime = LocalDateTime.parse("2019-06-09T12:23:01");
        Price price = Price.builder()
                .id("my-id")
                .symbol("USD")
                .created(localDateTime)
                .rate(23.00d)
                .build();

        when(priceService.getHistoricalPrices(anyString(), anyString())).thenReturn(Collections.singletonList(price));

        mockMvc.perform(get("/prices")
                .param("startDate", "2019-06-09")
                .param("endDate", "2019-06-09"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].id").value("my-id"))
                .andExpect(jsonPath("$[0].symbol").value("USD"))
                .andExpect(jsonPath("$[0].created").value(localDateTime.toString()))
                .andExpect(jsonPath("$[0].rate").value(23.00d))
                .andDo(document("historical-prices",
                        requestParameters(
                                parameterWithName("startDate").description("the start date to get prices"),
                                parameterWithName("endDate").description("the end date to get prices")
                        )
                ));
    }

    @Test
    @DisplayName("cannot parse dates returns DateTimeParseException")
    void dateParse() throws Exception {

        when(priceService.getHistoricalPrices(anyString(), anyString())).thenThrow(DateTimeParseException.class);

        mockMvc.perform(get("/prices")
                .param("startDate", "invalid-date")
                .param("endDate", "invalid-date"))
                .andExpect(status().isBadRequest())
                .andDo(document("error-date-parse",
                        requestParameters(
                                parameterWithName("startDate").description("the start date to get prices"),
                                parameterWithName("endDate").description("the end date to get prices")
                        )
                ));
    }

    @Test
    @DisplayName("invalid date range returns InvalidDateRangeException")
    void dateRange() throws Exception {

        when(priceService.getHistoricalPrices(anyString(), anyString())).thenThrow(InvalidDateRangeException.class);

        mockMvc.perform(get("/prices")
                .param("startDate", "2019-06-10")
                .param("endDate", "2019-06-09"))
                .andExpect(status().isBadRequest())
                .andDo(document("error-invalid-range",
                        requestParameters(
                                parameterWithName("startDate").description("the start date to get prices"),
                                parameterWithName("endDate").description("the end date to get prices")
                        )
                ));
    }

    @Test
    @DisplayName("price not found returns PriceNotFoundException")
    void notFound() throws Exception {

        when(priceService.getHistoricalPrices(anyString(), anyString())).thenThrow(PriceNotFoundException.class);

        mockMvc.perform(get("/prices")
                .param("startDate", "2019-06-09")
                .param("endDate", "2019-06-09"))
                .andExpect(status().isBadRequest())
                .andDo(document("error-no-price",
                        requestParameters(
                                parameterWithName("startDate").description("the start date to get prices"),
                                parameterWithName("endDate").description("the end date to get prices")
                        )
                ));
    }
}